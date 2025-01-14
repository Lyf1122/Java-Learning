package org.generate.api;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MysqlParser implements Parser{
  // 用于解析 CREATE TABLE SQL 语句的正则表达式
  private static final String SQL_PATTERN =
    "CREATE TABLE `([a-zA-Z_]+)`\\s*\\(([^;]+)\\);";

  private static final String FIELD_PATTERN =
    "`([a-zA-Z_]+)`\\s*(varchar|int|text|datetime|float|double|boolean)";

  @Override
  public String getClassName(String input) {
    // 获取表名作为 DTO 类的名称
    Pattern pattern = Pattern.compile(SQL_PATTERN);
    Matcher matcher = pattern.matcher(input);
    if (matcher.find()) {
      return capitalize(matcher.group(1)); // 表名作为 DTO 类名
    }
    throw new IllegalArgumentException("Invalid SQL input.");
  }

  @Override
  public String parse(String input) {
    // 从 SQL 语句中解析表结构并生成 DTO 类代码
    Pattern pattern = Pattern.compile(SQL_PATTERN);
    Matcher matcher = pattern.matcher(input);

    if (matcher.find()) {
      String tableName = matcher.group(1); // 表名
      String columnsDefinition = matcher.group(2); // 字段定义部分

      // DTO 类头部信息
      StringBuilder sb = new StringBuilder();
      String className = capitalize(tableName);
      sb.append("public class ").append(className).append(" {\n");

      // 解析字段定义并生成相应的成员变量
      Pattern fieldPattern = Pattern.compile(FIELD_PATTERN);
      Matcher fieldMatcher = fieldPattern.matcher(columnsDefinition);

      while (fieldMatcher.find()) {
        String fieldName = fieldMatcher.group(1); // 字段名
        String fieldType = fieldMatcher.group(2); // 字段类型

        // 映射 MySQL 数据类型到 Java 类型
        String javaType = mapSqlTypeToJavaType(fieldType);

        sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n");

        // 生成 getter 和 setter 方法
        sb.append("\n    public ").append(javaType).append(" get")
          .append(capitalize(fieldName)).append("() {\n")
          .append("        return ").append(fieldName).append(";\n")
          .append("    }\n");

        sb.append("\n    public void set").append(capitalize(fieldName))
          .append("(").append(javaType).append(" ")
          .append(fieldName).append(") {\n")
          .append("        this.").append(fieldName).append(" = ")
          .append(fieldName).append(";\n")
          .append("    }\n");
      }

      sb.append("\n}");

      return sb.toString();
    } else {
      throw new IllegalArgumentException("Invalid SQL input.");
    }
  }

  private String mapSqlTypeToJavaType(String sqlType) {
    // 映射 MySQL 类型到 Java 类型
    switch (sqlType.toLowerCase()) {
      case "varchar":
      case "text":
        return "String";
      case "int":
        return "int";
      case "datetime":
        return "java.util.Date";  // 可以根据需求调整为 LocalDateTime
      case "float":
        return "float";
      case "double":
        return "double";
      case "boolean":
        return "boolean";
      default:
        throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
    }
  }

  private String capitalize(String str) {
    // 将字段名首字母大写，生成 getter 和 setter 方法时使用
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}
