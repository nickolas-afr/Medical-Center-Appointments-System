package niko.hospitalmanagementsystem;

public class CenterText {

    public static String centerText(String text, int width) {
        if (text == null) text = ""; // Handle null values gracefully
        int padding = (width - text.length()) / 2;
        int extraPadding = (width - text.length()) % 2; // Handle odd widths
        return " ".repeat(padding) + text + " ".repeat(padding + extraPadding);
    }
}
