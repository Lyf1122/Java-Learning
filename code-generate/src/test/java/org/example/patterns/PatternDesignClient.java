package org.example.patterns;

import org.example.patterns.adapter.*;
import org.junit.jupiter.api.Test;

public class PatternDesignClient {
    @Test
    public void adapterClient() {
        TypeC newComputer = new Computer();
        newComputer.chargeWithTypeC();
        System.out.println("---Switch to USB Charging---");
        USB usbAdapter = new AdapterCharger();
        usbAdapter.charge();
    }
}
