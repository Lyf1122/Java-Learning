package org.generate.api;

public interface Parser {
  String getClassName(String input);
  String parse(String input);
}
