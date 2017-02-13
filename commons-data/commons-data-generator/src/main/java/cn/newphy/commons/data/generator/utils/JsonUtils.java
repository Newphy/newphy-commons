package cn.newphy.commons.data.generator.utils;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Administrator on 14-2-19.
 */
public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 支持序列化hibernate entity
        //objectMapper.registerModule(new Hibernate3Module());
    }

    /**
     * 通过json读取对象.
     *
     * @param json
     * @param objectClass
     * @param <T>
     * @return
     * @throws java.io.IOException
     */
    public static <T> T readObject(String json, Class<T> objectClass) throws IOException {
        return objectMapper.readValue(json, objectClass);
    }

    /**
     * json读取List对象
     *
     * @param json
     * @param elementClass
     * @param <T>
     * @return
     * @throws java.io.IOException
     */
    public static <T> List<T> readList(String json, Class<T> elementClass) throws IOException {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
        return objectMapper.readValue(json, listType);
    }

    /**
     * json读取Map对象
     *
     * @param json
     * @param keyClass
     * @param valueClass
     * @param <K>
     * @param <V>
     * @return
     * @throws java.io.IOException
     */
    public static <K, V> Map<K, V> readMap(String json, Class<K> keyClass, Class<V> valueClass) throws IOException {
        JavaType listType = objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
        return objectMapper.readValue(json, listType);
    }


    /**
     * 将对象转为Json
     *
     * @param obj
     * @return
     * @throws java.io.IOException
     */
    public static String toJSON(Object obj) throws IOException {
        if (obj == null) {
            return "";
        }
        return objectMapper.writeValueAsString(obj);
    }
    
}
