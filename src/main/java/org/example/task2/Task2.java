package org.example.task2;


public class Task2 {

    /**
     *
     * Если брать предел lim n->inf (1/n!) * (1! + 2! + 3! + ... + n!)
     * Мы получим неопределённость бесконечность/бесконечность.
     * Поделим на самую быстро растущую степень (n!) все части.
     * Константы (1!+2!+3! ... )  при делении на бесконечно большое - дадут 0,
     * n! при делении на n! даст 1.
     * ==> lim n -> inf функции = (1 + 0)/1 = 1; Функция стремится к 1;
     *
     *
     */

    static long NFactorial = 1; //Переменная для сохранения последнего факториала, что бы не приходилось пересчитывать

    public static void main(String[] args) {

        int n = 20;
        System.out.println(Fact(n));
    }

    private static double Fact(int n){
        return (double) calcFactRow(n) / NFactorial;
    }

    /**
     *
     * Вычисление и вывод суммы прогрессии
     *
     */
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
