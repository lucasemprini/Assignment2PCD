package utility;

public final class StringUtilities {
    private StringUtilities() {}

    public static boolean isFileTextual(final String fileName) {
        return fileName.endsWith("java")
                || fileName.endsWith("txt")
                || fileName.endsWith("xml")
                || fileName.endsWith("md")
                || fileName.endsWith(".c")
                || fileName.endsWith(".h")
                || fileName.endsWith(".cpp")
                || fileName.endsWith(".hpp")
                || fileName.endsWith("html")
                || fileName.endsWith("css")
                || fileName.endsWith("js")
                || fileName.endsWith("sql")
                || fileName.endsWith("sh");
    }

    public static String treePath(final String path) {
        final String regex = "(/)|(\\\\)";
        final String toReturn = path.replaceAll(regex, "\n\\\\");
        return toReturn.substring(2);
    }
}
