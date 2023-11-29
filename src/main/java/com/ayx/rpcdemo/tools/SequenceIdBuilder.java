package com.ayx.rpcdemo.tools;

import java.util.concurrent.atomic.AtomicLong;

//生成sequenceId
public class SequenceIdBuilder {

    private static AtomicLong sequenceId = new AtomicLong(0);

    public static int getSequenceId(){
        return (int) sequenceId.getAndIncrement();
    }
}
