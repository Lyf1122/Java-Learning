package org.example.patterns.builder;

public class Director {
    private BicycleBuilder builder;

    public Director(BicycleBuilder builder) {
        this.builder = builder;
    }

    public Bicycle construct() {
        builder.buildFrame();
        builder.buildTires();
        return builder.getBicycle();
    }

}
