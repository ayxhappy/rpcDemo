package com.ayx.rpcdmeo;

import com.ayx.rpcdemo.message.RpcRequestMessage;
import com.ayx.rpcdemo.protocol.ServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcDemoTest {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(
                0,
                "com.ayx.rpcdemo.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );

        //server端反射调用
        Class<?> clazz = Class.forName(rpcRequestMessage.getInterfaceName());
        Object obj = ServiceFactory.get(clazz);

        Method method = clazz.getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());

        Object result = method.invoke(obj, rpcRequestMessage.getParameterValue());

        System.out.println(result);
    }
}
