package org.example.patterns.factory;

public class SquareBlockFactory implements BlockFactory{
    @Override
    public Block createBlock() {
        return new SquareBlock();
    }
}
