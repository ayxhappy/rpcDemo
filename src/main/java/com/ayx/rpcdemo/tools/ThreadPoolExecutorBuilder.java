package com.ayx.rpcdemo.tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorBuilder {

    private static ThreadPoolExecutor executor = null;
    private static final Object LOCK = new Object();

    static {
        executor = init();
    }

    private static ThreadPoolExecutor init() {
        return new ThreadPoolExecutor(
                20,
                200,
                3000,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("rpc-server-thread");
                    return thread;
                }
        );
    }

    public static ThreadPoolExecutor builder() {
        //使用双重检查所保证单例
        if (executor != null) {
            return executor;
        }

        synchronized (LOCK) {
            if (executor != null) {
                return executor;
            }
            return init();
        }
    }
}
