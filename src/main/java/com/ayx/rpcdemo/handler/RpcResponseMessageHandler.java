package com.ayx.rpcdemo.handler;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.ayx.rpcdemo.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static final Map<Integer, Promise> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("RPC调用结果：{}", msg);
        Promise promise = PROMISES.remove(msg.getSequenceId());
        Object returnValue = msg.getReturnValue();
        Exception exception = msg.getExceptionValue();
        if (exception == null) {
            log.info("returnValue:", returnValue);
            promise.setSuccess(returnValue);
        } else {
            promise.setFailure(msg.getExceptionValue());
        }

    }
}
