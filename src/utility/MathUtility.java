package utility;

public final class MathUtility {
    private  MathUtility() {}

    public static double roundAvoid(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }
}
