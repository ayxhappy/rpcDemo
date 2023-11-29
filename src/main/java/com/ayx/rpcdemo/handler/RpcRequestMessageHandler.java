package com.ayx.rpcdemo.handler;

import com.ayx.rpcdemo.config.SysConfig;
import com.ayx.rpcdemo.message.RpcRequestMessage;
import com.ayx.rpcdemo.message.RpcResponseMessage;
import com.ayx.rpcdemo.protocol.ServiceFactory;
import com.ayx.rpcdemo.tools.ThreadPoolExecutorBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private static ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder();
    private static int serviceTimeout = SysConfig.serviceTimeOut();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequestMessage) throws Exception {
        RpcResponseMessage message = invoke(rpcRequestMessage);

        Future<?> future = executor.submit(() -> {
            invoke(rpcRequestMessage);
        });

        try {
            future.get(serviceTimeout, TimeUnit.MILLISECONDS);
            ctx.writeAndFlush(message);
        } catch (Exception e) {
            //超时抛出超时逻辑
            message.setReturnValue(null); //TODO 这里有问题 get超时之后还能获取到任务的返回值
            message.setExceptionValue(new RuntimeException(e));
            ctx.writeAndFlush(message);
        }
    }

    private static RpcResponseMessage invoke(RpcRequestMessage rpcRequestMessage) {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            Class<?> clazz = Class.forName(rpcRequestMessage.getInterfaceName());
            Object obj = ServiceFactory.get(clazz);

            Method method = clazz.getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());

            Object result = method.invoke(obj, rpcRequestMessage.getParameterValue()); //方法返回值

            //封装resp对象发出去
            rpcResponseMessage.setSequenceId(rpcRequestMessage.getSequenceId());
            rpcResponseMessage.setReturnValue(result);
        } catch (Exception e) {
            rpcResponseMessage.setExceptionValue(e);
            throw new RuntimeException(e);
        }
        return rpcResponseMessage;
    }
}
