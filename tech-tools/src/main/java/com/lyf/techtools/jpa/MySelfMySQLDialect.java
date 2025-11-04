package com.lyf.techtools.jpa;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MySQLDialect;

public class MySelfMySQLDialect extends MySQLDialect {

  public MySelfMySQLDialect() {
    super();
  }

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    super.initializeFunctionRegistry(functionContributions);

    // 使用新的方式注册自定义函数
    var functionRegistry = functionContributions.getFunctionRegistry();

    // 注册 regexp 函数
    functionRegistry.registerPattern(
      "regexp",
      "?1 regexp ?2"
    );

    // 注册 bitwise AND 函数
    functionRegistry.registerPattern(
      "bitwiseANDOperation",
      "?1 & ?2"
    );
  }
}
