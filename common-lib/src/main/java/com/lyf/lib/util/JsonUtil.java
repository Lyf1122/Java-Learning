package com.lyf.lib.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class JsonUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
  private static ObjectMapper MAP;

  private JsonUtil() {
  }

  private static void init() {
    MAP = new ObjectMapper();
    MAP.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    MAP.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  private static ObjectMapper map() {
    if (MAP == null) {
      init();
    }
    return MAP;
  }

  public static String render(Object obj) {
    if (obj == null) {
      return null;
    }
    ObjectWriter ow = map().writer();
    try {
      return ow.writeValueAsString(obj);
    } catch (Exception e) {
      logger.error("Failed to render object to json, [{}]", obj, e);
      throw new IllegalStateException("Failed to render object to json", e);
    }
  }

  public static String renderWithPrettyPrinter(Object obj) {
    if (obj == null) {
      return null;
    }
    ObjectWriter ow = map().writerWithDefaultPrettyPrinter();
    try {
      return ow.writeValueAsString(obj);
    } catch (Exception e) {
      logger.error("Failed to render object to json, [{}]", obj, e);
      throw new IllegalStateException("Failed to render object to json", e);
    }
  }

  public static <T> T parse(String json, Class<T> clazz) {

    try {
      return map().readValue(json, clazz);
    } catch (Exception e) {
      logger.error("Failed to parse json object, [{}]", json, e);
      throw new IllegalStateException("Failed to render object to json", e);
    }
  }

  public static <T> T parse(String json, TypeReference<T> listType) {
    try {
      return map().readValue(json, listType);
    } catch (Exception e) {
      logger.error("Failed to parse json list, [{}]", json, e);
      throw new IllegalStateException("Failed to parse json list", e);
    }
  }

  public static Map<String, Object> parseToMap(Object obj) {
    try {
      return map().convertValue(obj, new TypeReference<>() {});
    } catch (Exception e) {
      logger.error("Failed to parse obj to map, [{}]", obj, e);
      throw new IllegalStateException("Failed to parse json to map", e);
    }
  }

}
