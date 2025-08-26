package org.example.patterns.builder;

public class MountBicycle implements BicycleBuilder{

    private Bicycle bicycle;

    public MountBicycle() {
        this.bicycle = new Bicycle();
    }

    @Override
    public void buildFrame() {
        bicycle.setFrame("Aluminum Frame");
    }

    @Override
    public void buildTires() {
        bicycle.setTires("Knobby Tires");
    }

    @Override
    public Bicycle getBicycle() {
        return bicycle;
    }
}
