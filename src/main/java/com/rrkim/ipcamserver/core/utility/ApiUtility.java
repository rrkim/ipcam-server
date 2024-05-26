package com.rrkim.ipcamserver.core.utility;

import java.util.HashMap;
import java.util.Map;

public class ApiUtility {

    public static <T> Map<String, Object> getDataResponse(T data) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("data", data);

        return paramMap;
    }

    public static <T> Map<String, Object> getMessageResponse(String message) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("message", message);

        return getDataResponse(paramMap);
    }
}
