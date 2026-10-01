package ru.student.qa;

public final class Calculator {
    private Calculator() {}
    public static double square(double x) { return x * x; }
    public static double safeDivide(double a, double b) {
        if (b == 0) throw new IllegalArgumentException("division by zero");
        return a / b;
    }
}
