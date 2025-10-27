package org.example.patterns.singleton;

public enum EnumSingleton {
  INSTANCE;

  // 可以添加业务方法
  public void doSomething() {
    System.out.println("枚举单例方法执行");
  }

  // 可以持有状态
  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
