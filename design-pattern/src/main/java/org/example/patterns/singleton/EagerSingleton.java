package org.example.patterns.singleton;

public class EagerSingleton {
  // Thread Safe
  private static final EagerSingleton INS = new EagerSingleton();

  private EagerSingleton() {}

  public static EagerSingleton getInstance() {
    return INS;
  }
}
