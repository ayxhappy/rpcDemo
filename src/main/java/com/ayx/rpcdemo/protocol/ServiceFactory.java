package com.ayx.rpcdemo.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceFactory {

    private static Properties properties = new Properties();
    private static Map<Class<?>, Object> map = new ConcurrentHashMap<>();

    static {
        try (InputStream inputStream = ServiceFactory.class.getResourceAsStream("/application.properties")) {
            properties.load(inputStream);
            for (String classInterfaceName : properties.stringPropertyNames()) {
                if (classInterfaceName.endsWith("Service")) {
                    Class<?> intefaceClass = Class.forName(classInterfaceName);
                    Class<?> instaceClass = Class.forName(properties.getProperty(classInterfaceName));
                    map.put(intefaceClass, instaceClass.getDeclaredConstructor().newInstance());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object get(Class<?> intefaceClass) {
        return map.get(intefaceClass);
    }
}
