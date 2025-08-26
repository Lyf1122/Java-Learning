package org.example.patterns.strategy;

public class DiscountContext {

    private ShoppingDiscountStrategy shoppingDiscountStrategy;

    public void setShoppingDiscountStrategy(ShoppingDiscountStrategy discountStrategy) {
        this.shoppingDiscountStrategy = discountStrategy;
    }

    public double calculateDiscount(double price) {
        return shoppingDiscountStrategy.calculateDiscount(price);
    }

}
