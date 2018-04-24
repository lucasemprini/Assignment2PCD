package utility;

public final class StringUtilities {
    private StringUtilities() {}

    public static boolean isFileTextual(final String fileName) {
        return fileName.endsWith("java")
                || fileName.endsWith("txt")
                || fileName.endsWith("xml")
                || fileName.endsWith("md");
    }

    public static String treePath(final String path) {
        final String regex = "(/)|(\\\\)";
        return path.replaceAll(regex, "\n\\\\");
    }
}
