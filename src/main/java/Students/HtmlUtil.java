package Students;

/**
 * Utility class for escaping HTML to prevent XSS attacks
 */
public class HtmlUtil {

    /**
     * Escapes HTML special characters to prevent XSS
     * @param input The input string to escape
     * @return The escaped string safe for HTML output
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp")
                   .replace("<", "&lt")
                   .replace(">", "&gt")
                   .replace("\"", "&quot")
                   .replace("'", "&#x27")
                   .replace("/", "&#x2F");
    }
}