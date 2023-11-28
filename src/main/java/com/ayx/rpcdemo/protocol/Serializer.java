package com.ayx.rpcdemo.protocol;

import com.google.gson.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 序列化接口
 */
public interface Serializer {

    //反序列化方法
    <T> T deSerializer(Class<T> clazz, byte[] bytes);

    //序列化方法
    <T> byte[] serializer(T object);


    ;//定义枚举实现

    enum SerializerImpl implements Serializer {

        Java {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> byte[] serializer(T object) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return bos.toByteArray();
            }
        },
        Json {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                //return new Gson().fromJson(str, clazz);
                return new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create().fromJson(str, clazz);
            }

            @Override
            public <T> byte[] serializer(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
                //String json = new Gson().toJson(object);
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        },
        Hession {
            @Override
            public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
                return null;
            }

            @Override
            public <T> byte[] serializer(T object) {
                return null;
            }
        }
    }

    //Gson的关于Class类型的转换器

    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override             //   String.class
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}
