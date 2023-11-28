package com.ayx.rpcdemo.message;

import lombok.Data;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    // 省略旧的代码
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 102;

    private int sequenceId;

    static {
        // ...
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }

    public abstract int getMessageType();

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

}