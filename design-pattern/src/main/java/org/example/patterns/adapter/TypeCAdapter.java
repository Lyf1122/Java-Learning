package org.example.patterns.adapter;

public class TypeCAdapter implements USB{

    private TypeC typeC;

    public TypeCAdapter(TypeC typeC) {
        this.typeC = typeC;
    }

    @Override
    public void charge() {
        System.out.println("采用适配器充电");
        typeC.chargeWithTypeC();
    }
}
