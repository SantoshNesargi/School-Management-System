package Students.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tiny dependency-free JSON writer. We avoid pulling in Jackson/Gson because
 * the project has no Maven; this builder is sufficient for the small payloads
 * the API returns. Values that contain HTML-unsafe characters are escaped.
 */
public final class Json {

    private final StringBuilder sb = new StringBuilder();
    private boolean first = true;

    public static Json object() {
        Json j = new Json();
        j.sb.append('{');
        return j;
    }

    public static Json array() {
        Json j = new Json();
        j.sb.append('[');
        return j;
    }

    public Json put(String key, String value) {
        appendComma();
        sb.append('"').append(escape(key)).append('"').append(':');
        sb.append(value == null ? "null" : "\"" + escape(value) + "\"");
        return this;
    }

    public Json put(String key, Number value) {
        appendComma();
        sb.append('"').append(escape(key)).append('"').append(':');
        sb.append(value == null ? "null" : value.toString());
        return this;
    }

    public Json put(String key, boolean value) {
        appendComma();
        sb.append('"').append(escape(key)).append('"').append(':');
        sb.append(value);
        return this;
    }

    /** Convenience: put only if value is non-null. */
    public Json putIfPresent(String key, String value) {
        if (value == null) return this;
        return put(key, value);
    }

    public Json putRaw(String key, String rawJsonValue) {
        appendComma();
        sb.append('"').append(escape(key)).append('"').append(':');
        sb.append(rawJsonValue == null ? "null" : rawJsonValue);
        return this;
    }

    public Json end() {
        sb.append(first ? "}" : "}");
        first = false;
        return this;
    }

    public Json endArray() {
        sb.append(']');
        first = false;
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    private void appendComma() {
        if (!first) sb.append(',');
        first = false;
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': out.append("\\\\"); break;
                case '"':  out.append("\\\""); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else if (c == '<' || c == '>' || c == '&' || c == '\'') {
                        // Reduce XSS surface even inside JSON strings — most
                        // front-ends use these inside innerHTML, so encode.
                        out.append("\\u").append(String.format("%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    /** Convert a small object graph (Map, List, String, Number, Boolean, null) to JSON. */
    public static String stringify(Object o) {
        if (o == null) return "null";
        if (o instanceof String s) return "\"" + escape(s) + "\"";
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        if (o instanceof Map<?, ?> m) return mapToJson(m);
        if (o instanceof List<?> l) return listToJson(l);
        return "\"" + escape(o.toString()) + "\"";
    }

    private static String mapToJson(Map<?, ?> m) {
        Json j = object();
        for (Map.Entry<?, ?> e : m.entrySet()) {
            j.putRaw(String.valueOf(e.getKey()), stringify(e.getValue()));
        }
        return j.end().toString();
    }

    private static String listToJson(List<?> l) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : l) {
            if (!first) sb.append(',');
            first = false;
            sb.append(stringify(item));
        }
        sb.append(']');
        return sb.toString();
    }

    public static Map<String, Object> errorBody(String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", false);
        m.put("error", message);
        return m;
    }
}
