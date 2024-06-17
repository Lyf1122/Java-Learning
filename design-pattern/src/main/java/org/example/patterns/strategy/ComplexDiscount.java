package org.example.patterns.strategy;

public class ComplexDiscount extends ShoppingDiscountStrategy{
    private int[] thresholds = {100, 150, 200, 300};
    private int[] discounts = {5, 15, 25, 40};

    @Override
    public double calculateDiscount(double price) {
        for (int i = thresholds.length - 1; i >= 0; i--) {
            if (price >= thresholds[i]) {
                return price - discounts[i];
            }
        }
        return price;
    }
}
