package org.example.patterns.singleton;

public class InnerClassSingleton {
  private InnerClassSingleton() {}

  private static class SingletonHolder {
    private static final InnerClassSingleton INS = new InnerClassSingleton();
  }

  public static InnerClassSingleton getInstance() {
    return SingletonHolder.INS;
  }
}
