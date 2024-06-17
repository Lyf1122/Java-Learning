package org.example.patterns.adapter;

public class AdapterCharger implements USB{
    @Override
    public void charge() {
        System.out.println("USB Adapter: Charging with USB");
    }
}
