package org.example;

import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            // int a = in.nextInt();
            // int b = in.nextInt();
            // System.out.println(a + b);
            int n = in.nextInt();

            List<Double> problities = new LinkedList<>();
            while(n > 0) {
                n--;
                problities.add(in.nextDouble());
            }
            System.out.println(calculate(problities));
        }
    }

    static double calculate(List<Double> pros) {
        double result = 0.00000000;
        for (int i = 0; i<pros.size(); i++) {
            double p = pros.get(i);
            if (p == 0 || p == 1) {
                result += 1;
            } else {
                result = result + (1- p);
            }
        }

        return result;
    }

}