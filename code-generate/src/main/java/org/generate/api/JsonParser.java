package org.generate.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component
public class JsonParser implements Parser{

  private final ObjectMapper mapper;

  public JsonParser(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String getClassName(String input) {
    try {
      JsonNode rootNode = mapper.readTree(input);
      return rootNode.get("className").asText();
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }

  @Override
  public String parse(String input) {
    try {
      JsonNode rootNode = mapper.readTree(input);
      StringBuilder sb = new StringBuilder();
      String className = rootNode.get("className").asText();
      sb.append("public class ").append(className).append(" {\n");
      JsonNode fields = rootNode.get("fields");
      Iterator<Map.Entry<String, JsonNode>> fieldIterator = fields.fields();
      while (fieldIterator.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldIterator.next();
        String fieldName = fieldEntry.getKey();
        JsonNode fieldType = fieldEntry.getValue();
        sb.append("    private ").append(fieldType.asText()).append(" ").append(fieldName).append(";\n");
        sb.append("\n    public ").append(fieldType).append(" get").append(capitalize(fieldName)).append("() {\n")
          .append("        return ").append(fieldName).append(";\n")
          .append("    }\n");

        sb.append("\n    public void set").append(capitalize(fieldName)).append("(").append(fieldType).append(" ")
          .append(fieldName).append(") {\n")
          .append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n")
          .append("    }\n");
      }
      sb.append("\n}");
      return sb.toString();
    } catch (IOException e)
      {
        throw new RuntimeException("Failed to parse JSON", e);
      }
  }

  private String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

}
