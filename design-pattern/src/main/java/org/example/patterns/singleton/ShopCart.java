package org.example.patterns.singleton;

import java.util.ArrayList;
import java.util.List;

public class ShopCart {

    private static volatile ShopCart instance;

    private static List<String> itemList = new ArrayList<>();
    private static List<Integer> quantityList = new ArrayList<>();

    private ShopCart() {
        // 私有构造方法，防止外部直接new
    }

    public static ShopCart getInstance() {
        if (instance == null) {
            synchronized (ShopCart.class){
                if (instance == null) {
                    instance = new ShopCart();
                }
            }
        }
        return instance;
    }

}
