package com.ayx.rpcdemo.client;

import com.ayx.rpcdemo.handler.RpcResponseMessageHandler;
import com.ayx.rpcdemo.message.RpcRequestMessage;
import com.ayx.rpcdemo.protocol.MessageCodecSharable;
import com.ayx.rpcdemo.protocol.ProcotolFrameDecoder;
import com.ayx.rpcdemo.service.HelloService;
import com.ayx.rpcdemo.tools.SequenceIdBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class RpcChannelManger {
    private static Channel channel = null;

    static {
        init();
    }

    private static void init() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(RPC_HANDLER);
                }
            });
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        //HelloService helloService = null;
        //helloService.sayHello("zhangsan");


        HelloService helloService = getProxyService(HelloService.class);
        String result = helloService.sayHello("zhangsan");
        System.out.println("代理生成结果:" + result);

    }

    private static <T> T getProxyService(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        //使用动态代理生成代理类
        Object obj = Proxy.newProxyInstance(classLoader, new Class[]{clazz}, (proxy, method, args1) -> {

            //发送RPC请求
            int sequenceId = SequenceIdBuilder.getSequenceId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    HelloService.class.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args1);
            channel.writeAndFlush(message);

            // 3. 准备一个空 Promise 对象，来接收结果             指定 promise 对象异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            }
            throw new RuntimeException(promise.cause());
        });
        return (T) obj;
    }
}