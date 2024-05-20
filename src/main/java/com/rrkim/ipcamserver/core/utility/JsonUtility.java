package com.rrkim.ipcamserver.core.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtility {

    public static <T> String convertJson(T map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    // JSON 문자열에서 주어진 키에 해당하는 값을 문자열로 반환하는 메소드
    public static String getProperty(String jsonString, String key) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // jsonString을 Map으로 변환
        Map<String,Object> map = objectMapper.readValue(jsonString, Map.class);

        // 주어진 키에 해당하는 값을 Object로 가져옴
        Object value = map.get(key);

        // 값이 null이 아니라면, 해당 값을 문자열로 변환하여 반환
        if (value != null) {
            return value.toString();
        }

        // 키에 해당하는 값이 없다면 null을 반환
        return null;
    }
}
