package org.faicai.d4c.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtil {
    public static final ObjectMapper JSON_MAPPER = newObjectMapper();

    private static ObjectMapper newObjectMapper() {
        ObjectMapper result = new ObjectMapper();
        result.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        result.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        result.setSerializationInclusion(Include.NON_NULL);
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return result;
    }

    public static ObjectMapper getObjectMapper() {
        return JSON_MAPPER;
    }


    public static String writeValueAsString(Object value) {
        try {
            return value == null ? null : JSON_MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e); // TIP: 原则上，不对异常包装，这里为什么要包装？因为正常情况不会发生IOException
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object value) throws IllegalArgumentException {
        return convertValue(value, Map.class);
    }


    public static <T> T convertValue(Object value, Class<T> clazz) throws IllegalArgumentException {
        if (StringUtils.isEmpty(value)) return null;
        try {
            if (value instanceof String)
                value = JSON_MAPPER.readTree((String) value);
            return JSON_MAPPER.convertValue(value, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> List<T> convertList(Object value, Class<T> clazz) {
        List list = convertValue(value, List.class);
        return (List<T>) list.stream().map(o -> convertValue(o, clazz)).collect(Collectors.toList());
    }


}


