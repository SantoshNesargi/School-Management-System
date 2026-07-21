package Students;

/**
 * Output-encoding helpers. Use {@link #escapeHtml} for any value that will
 * be inserted into an HTML body or attribute, and {@link #escapeJs} for
 * values that go inside an inline {@code <script>}. Both methods are
 * null-safe and return an empty string for null input.
 */
public class HtmlUtil {

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

    /**
     * Escape a value for inclusion in a single-quoted or double-quoted
     * JavaScript string literal (for example in an inline script that
     * bootstraps data). Equivalent to the OWASP "encode for JavaScript"
     * rule: backslash, single quote, double quote, forward slash, line
     * terminators, and {@code <} to break out of a script context.
     */
    public static String escapeJs(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(input.length() + 8);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\': out.append("\\\\"); break;
                case '\'': out.append("\\'");  break;
                case '"':  out.append("\\\""); break;
                case '/':  out.append("\\/");  break;
                case '<':  out.append("\\u003c"); break;
                case '>':  out.append("\\u003e"); break;
                case '&':  out.append("\\u0026"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case ' ': out.append("\\u2028"); break;
                case ' ': out.append("\\u2029"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
