package org.example.patterns.strategy;

public class DirectDiscount extends ShoppingDiscountStrategy{
    @Override
    public double calculateDiscount(double price) {
        return 0.9 * price;
    }
}
