package org.example.patterns.builder;

public interface BicycleBuilder {
    void buildFrame();
    void buildTires();
    Bicycle getBicycle();
}
