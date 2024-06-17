package org.example.patterns.adapter;

public class Computer implements TypeC{
    @Override
    public void chargeWithTypeC() {
        System.out.println("Charging with TypeC");
    }
}
