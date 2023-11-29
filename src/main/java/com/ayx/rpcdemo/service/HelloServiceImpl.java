package com.ayx.rpcdemo.service;

import java.util.concurrent.TimeUnit;

public class HelloServiceImpl implements HelloService{

    @Override
    public String sayHello(String name) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "hello ! " + name;
    }
}
