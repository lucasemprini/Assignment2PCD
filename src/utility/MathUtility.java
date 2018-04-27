package utility;

public final class MathUtility {
    private  MathUtility() {}

    /**
     * Metodo che arrotonda un double a due cifre decimali.
     * @param value il double da arrotondare.
     * @return il double arrotondato a due cifre decimali.
     */
    public static double roundAvoid(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }
}
