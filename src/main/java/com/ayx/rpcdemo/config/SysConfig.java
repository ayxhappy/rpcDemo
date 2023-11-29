package com.ayx.rpcdemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SysConfig {

    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream in = SysConfig.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getSerializerType() {
        String serializerMethod = (String) properties.get("serializerMethod");
        int serializerType = 0;
        switch (serializerMethod) {
            case "Java":
                serializerType = 0;
                break;
            case "Json":
                serializerType = 1;
                break;
            case "Hession":
                serializerType = 2;
                break;
        }
        return serializerType;
    }

    public static int serviceTimeOut(){
        return Integer.parseInt(properties.getProperty("serviceTimeout"));
    }
}
