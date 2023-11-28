package com.ayx.rpcdemo.handler;

import com.ayx.rpcdemo.message.RpcRequestMessage;
import com.ayx.rpcdemo.message.RpcResponseMessage;
import com.ayx.rpcdemo.protocol.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequestMessage) throws Exception {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            Class<?> clazz = Class.forName(rpcRequestMessage.getInterfaceName());
            Object obj = ServiceFactory.get(clazz);

            Method method = clazz.getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());

            Object result = method.invoke(obj, rpcRequestMessage.getParameterValue()); //方法返回值

            //封装resp对象发出去
            rpcResponseMessage.setReturnValue(result);
        } catch (Exception e) {
            rpcResponseMessage.setExceptionValue(e);
            throw new RuntimeException(e);
        }

        ctx.writeAndFlush(rpcResponseMessage);
    }
}
