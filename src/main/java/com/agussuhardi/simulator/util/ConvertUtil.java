package com.agussuhardi.simulator.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConvertUtil {

  public static Object mapToObject(Map<Object, Object> map) {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(map, Object.class);
  }
  public static Object mapToObjectString(Map<String, String> map) {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(map, Object.class);
  }

  public static Map<String, Object> objectToMap(Object obj) {
    Map<String, Object> map = new HashMap<>();
    var fieldValues = obj.getClass().getDeclaredFields();
    for (Field field : fieldValues) {

      field.setAccessible(true);
      try {

        var value = field.get(obj);
        map.put(field.getName(), value);
      } catch (Exception ignore) {

      }
    }
    return map;
  }

  public static Map<String, Object> jsonToMap(String json) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(json, new TypeReference<>() {});
    } catch (Exception e) {
//      e.printStackTrace();
    }
    return new HashMap<>();
  }

  public static Map<String, String> jsonToMapString(String json) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(json, new TypeReference<>() {});
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  public static Map<String, Object> base64ToMap(String base64) throws IOException {
    byte[] decoded = Base64.decodeBase64(base64);
    String json = new String(decoded, StandardCharsets.UTF_8);
    return new ObjectMapper().readValue(json, new TypeReference<>() {});
  }

  public Map<String, Object> json(String json) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(json, new TypeReference<>() {});
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }
}
