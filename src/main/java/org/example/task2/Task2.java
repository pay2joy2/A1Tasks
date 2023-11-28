package org.example.task2;


public class Task2 {

    static long NFactorial = 1;

    public static void main(String[] args) {
        int n = 20;

        System.out.println(Fact(n));
    }

    private static double Fact(int n){
        return (double) calcFactRow(n) / NFactorial;
    }


    private static long calcFactRow (int n){
        long sum = 0;
        long fac = 1;
        for (int i = 1; i <= n; i++) {
            System.out.print("Fac[" + i + "] = " + (fac = fac*i) + " | ");
            System.out.print("Sum is : " + (sum += fac) + "\n");
        }
        System.out.print("Final sum is : " + sum + "\n");
        NFactorial = fac;
        return sum;
    }

}
