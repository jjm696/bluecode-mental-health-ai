package com.psychology.assistant.util;

import java.util.ArrayList;
import java.util.List;

public class JsonArrayUtil {

    private JsonArrayUtil() {
    }

    public static List<String> parseJsonArray(String source) {
        List<String> result = new ArrayList<String>();
        if (source == null || source.trim().isEmpty()) {
            return result;
        }

        String text = source.trim();
        if (text.startsWith("[")) {
            text = text.substring(1);
        }
        if (text.endsWith("]")) {
            text = text.substring(0, text.length() - 1);
        }
        if (text.trim().isEmpty()) {
            return result;
        }

        String[] items = text.split(",");
        for (String item : items) {
            String cleaned = item.trim();
            if (cleaned.startsWith("\"")) {
                cleaned = cleaned.substring(1);
            }
            if (cleaned.endsWith("\"")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            cleaned = cleaned.trim();
            if (!cleaned.isEmpty() && !"null".equalsIgnoreCase(cleaned)) {
                result.add(cleaned);
            }
        }
        return result;
    }
}
