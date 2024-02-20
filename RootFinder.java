import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RootFinder {

    public static List<Double> rootFinder(List<Double> equation, double error) {
        List<List<Double>> derivatives = new ArrayList<>();
        derivatives.add(equation);
        for (int i = 0; i < equation.size() - 3; i++) {
            derivatives.add(takeDerivative(derivatives.get(i)));
        }

        List<Double> roots = quadraticFormula(derivatives.get(derivatives.size() - 1));
        if (roots.size() != 2) {
            System.out.println("Could not find roots :(");
            return null;
        }
        derivatives.remove(derivatives.size() - 1);
        for (int i = derivatives.size() - 1; i >= 0; i--) {
            List<Double> newRoots = new ArrayList<>();
            newRoots.add(newtonsMethod(derivatives.get(i), roots.get(0) - 20, error));
            for (int j = 0; j < roots.size() - 1; j++) {
                double root;
                if (functionOutput(derivatives.get(i), roots.get(j)) > functionOutput(derivatives.get(i), roots.get(j + 1))) {
                    root = binarySearchDescending(derivatives.get(i), roots.get(j), roots.get(j + 1), error);
                } else {
                    root = binarySearchAscending(derivatives.get(i), roots.get(j), roots.get(j + 1), error);
                }
                if (!Double.isNaN(root)) {
                    newRoots.add(root);
                }
            }
            newRoots.add(newtonsMethod(derivatives.get(i), roots.get(roots.size() - 1) + 20, error));
            roots = newRoots;
        }
        return roots;
    }

    public static List<Double> takeDerivative(List<Double> equation) {
        List<Double> derivative = new ArrayList<>();
        int largestPower = equation.size() - 1;
        for (int i = 0; i < largestPower; i++) {
            int exponent = largestPower - i;
            derivative.add(exponent * equation.get(i));
        }
        return derivative;
    }

    public static List<Double> quadraticFormula(List<Double> equation) {
        double a = equation.get(0);
        double b = equation.get(1);
        double c = equation.get(2);
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return new ArrayList<>();
        } else if (discriminant == 0) {
            return Arrays.asList(-b / (2 * a));
        }
        double squareRoot = Math.sqrt(discriminant);
        return Arrays.asList((-b + squareRoot) / (2 * a), (-b - squareRoot) / (2 * a));
    }

    public static double binarySearchDescending(List<Double> equation, double left, double right, double error) {
        while (left <= right) {
            double mid = (left + right) / 2;
            double output = functionOutput(equation, mid);
            if (withinRange(output, 0, error)) {
                return mid;
            } else if (output > 0) {
                left = mid + error / 100;
            } else {
                right = mid - error / 100;
            }
        }
        return Double.NaN;
    }

    public static double binarySearchAscending(List<Double> equation, double left, double right, double error) {
        while (left <= right) {
            double mid = (left + right) / 2;
            double output = functionOutput(equation, mid);
            if (withinRange(output, 0, error)) {
                return mid;
            } else if (output < 0) {
                left = mid + error / 100;
            } else {
                right = mid - error / 100;
            }
        }
        return Double.NaN;
    }

    public static double functionOutput(List<Double> equation, double input) {
        int length = equation.size();
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum += equation.get(length - i - 1) * Math.pow(input, i);
        }
        return sum;
    }

    public static boolean withinRange(double num, double target, double range) {
        return num <= target + range && num >= target - range;
    }

    public static double newtonsMethod(List<Double> equation, double guess, double error) {
        List<Double> derivative = takeDerivative(equation);
        double delta = Math.abs(0 - functionOutput(equation, guess));
        while (delta > error) {
            guess = guess - functionOutput(equation, guess) / functionOutput(derivative, guess);
            delta = Math.abs(0 - functionOutput(equation, guess));
        }
        return guess;
    }
}