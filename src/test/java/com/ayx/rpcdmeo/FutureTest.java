package com.ayx.rpcdmeo;

import com.ayx.rpcdemo.tools.ThreadPoolExecutorBuilder;

import java.util.concurrent.*;

public class FutureTest {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder();

        Future<String> future = executor.submit((Callable<String>) () -> {
            TimeUnit.SECONDS.sleep(5);
            return "success;";
        });

        String result = null;
        try {
            result = future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
        System.out.println(result);
    }
}
