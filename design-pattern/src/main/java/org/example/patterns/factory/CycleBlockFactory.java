package org.example.patterns.factory;

public class CycleBlockFactory implements BlockFactory{
    @Override
    public Block createBlock() {
        return new CycleBlock();
    }
}
