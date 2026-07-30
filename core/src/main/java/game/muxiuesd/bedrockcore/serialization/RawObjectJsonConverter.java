package game.muxiuesd.bedrockcore.serialization;

import com.badlogic.gdx.utils.JsonWriter;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将RawObject转化成json的工具类
 * */
public class RawObjectJsonConverter {
    /**
     * 核心工具：将 RawObject 转换为 JSON 字符串
     */
    public static String toJson (RawObject raw) {
        JsonDataWriter writer = new JsonDataWriter();
        writeValue(writer, null, raw.unwrap()); // 直接取出原始对象开始递归
        return writer.getResult();
    }

    public static String toJson (JsonDataWriter writer, RawObject raw) {
        writeValue(writer, null, raw.unwrap()); // 直接取出原始对象开始递归
        return writer.getResult();
    }

    /**
     * 递归写入值。
     *
     * @param writer JsonDataWriter 实例
     * @param key    当前值的键（若为顶层或数组元素则为 null）
     * @param value  原始 Java 对象（Map、List、基本类型等）
     */
    private static void writeValue(JsonDataWriter writer, String key, Object value) {
        if (value instanceof Map<?, ?> map) {
            // 写入 JSON 对象
            if (key != null) {
                writer.objStart(key);
            } else {
                writer.objStart();
            }
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                // 键统一转为字符串
                writeValue(writer, String.valueOf(entry.getKey()), entry.getValue());
            }
            writer.objEnd();

        } else if (value instanceof List<?> list) {
            // 写入 JSON 数组
            if (key != null) {
                writer.arrayStart(key);
            } else {
                writer.arrayStart();
            }
            for (Object item : list) {
                // 数组元素没有键，传 null
                writeValue(writer, null, item);
            }
            writer.arrayEnd();

        } else {
            // 基本类型或 null
            if (key != null) {
                writeKeyedValue(writer, key, value);
            } else {
                writeUnkeyedValue(writer, value);
            }
        }
    }

    /**
     * 写入带键的基本类型值，根据实际类型调用对应的 writer 方法。
     */
    private static void writeKeyedValue(JsonDataWriter writer, String key, Object value) {
        if (value == null) {
            // 写入 null，直接操作底层 JsonWriter
            //writer.getWriter().getWriter().value(key, (Object) null);
        } else if (value instanceof String) {
            writer.writeString(key, (String) value);
        } else if (value instanceof Integer) {
            writer.writeInt(key, (Integer) value);
        } else if (value instanceof Long) {
            writer.writeLong(key, (Long) value);
        } else if (value instanceof Float) {
            writer.writeFloat(key, (Float) value);
        } else if (value instanceof Double) {
            writer.writeDouble(key, (Double) value);
        } else if (value instanceof Boolean) {
            writer.writeBoolean(key, (Boolean) value);
        } else if (value instanceof Short) {
            writer.writeShort(key, (Short) value);
        } else if (value instanceof Byte) {
            writer.writeByte(key, (Byte) value);
        } else if (value instanceof Character) {
            writer.writeChar(key, (Character) value);
        } else {
            // 未知类型降级为字符串
            writer.writeString(key, value.toString());
        }
    }

    /**
     * 写入无键的基本类型值（用于数组内部）。
     */
    private static void writeUnkeyedValue(JsonDataWriter writer, Object value) {
        try {
            JsonWriter jsonWriter = writer.getWriter().getWriter();
            if (value == null) {
                jsonWriter.value((Object) null);
            } else if (value instanceof String || value instanceof Number ||
                value instanceof Boolean || value instanceof Character) {
                // 数字、字符串、布尔、字符直接输出
                jsonWriter.value(value);
            } else {
                // 其他类型转为字符串
                jsonWriter.value(value.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心工具：将 JSON 字符串反序列化为 RawObject
     * @throws RuntimeException 若 JSON 格式非法
     */
    public static RawObject fromJson (String json) {
        Object parsed = parseValue(new StringReader(json));
        return wrapToRawObject(parsed);
    }

    /** 根据 Java 原始对象类型，调用对应的 RawObject 工厂方法 */
    private static RawObject wrapToRawObject(Object value) {
        if (value == null) {
            return RawObject.ofNull();
        } else if (value instanceof String) {
            return RawObject.ofString((String) value);
        } else if (value instanceof Integer) {
            return RawObject.ofInt((Integer) value);
        } else if (value instanceof Long) {
            return RawObject.ofLong((Long) value);
        } else if (value instanceof Double) {
            return RawObject.ofDouble((Double) value);
        } else if (value instanceof Boolean) {
            return RawObject.ofBoolean((Boolean) value);
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return RawObject.ofMap(map);
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            return RawObject.ofList(list);
        } else {
            // 未知类型降级为字符串（通常不会发生）
            return RawObject.ofString(value.toString());
        }
    }

    // ---------- 解析核心 ----------
    private static Object parseValue(StringReader r) {
        skipWhitespace(r);
        if (!r.hasNext()) throw new RuntimeException("Unexpected end of JSON");
        char c = r.peek();
        if (c == '"') return parseString(r);
        if (c == '{') return parseObject(r);
        if (c == '[') return parseArray(r);
        if (c == 't' || c == 'f') return parseBoolean(r);
        if (c == 'n') { parseNull(r); return null; }
        return parseNumber(r);
    }

    private static void skipWhitespace(StringReader r) {
        while (r.hasNext() && Character.isWhitespace(r.peek())) r.next();
    }

    private static String parseString(StringReader r) {
        r.next(); // 跳过开始双引号
        StringBuilder sb = new StringBuilder();
        while (r.hasNext()) {
            char c = r.next();
            if (c == '"') return sb.toString();
            if (c == '\\') {
                char n = r.next();
                switch (n) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        throw new RuntimeException("Unterminated string");
    }

    private static Map<String, Object> parseObject(StringReader r) {
        r.next(); // 跳过 '{'
        Map<String, Object> map = new LinkedHashMap<>();
        skipWhitespace(r);
        if (r.peek() == '}') { r.next(); return map; }
        while (true) {
            skipWhitespace(r);
            String key = parseString(r);
            skipWhitespace(r);
            if (r.next() != ':') throw new RuntimeException("Expected ':'");
            Object value = parseValue(r);
            map.put(key, value);
            skipWhitespace(r);
            char next = r.next();
            if (next == '}') break;
            if (next != ',') throw new RuntimeException("Expected ',' or '}'");
        }
        return map;
    }

    private static List<Object> parseArray(StringReader r) {
        r.next(); // 跳过 '['
        List<Object> list = new ArrayList<>();
        skipWhitespace(r);
        if (r.peek() == ']') { r.next(); return list; }
        while (true) {
            list.add(parseValue(r));
            skipWhitespace(r);
            char next = r.next();
            if (next == ']') break;
            if (next != ',') throw new RuntimeException("Expected ',' or ']'");
        }
        return list;
    }

    private static boolean parseBoolean(StringReader r) {
        if (r.peek() == 't') { consume(r, "true"); return true; }
        else { consume(r, "false"); return false; }
    }

    private static void parseNull(StringReader r) { consume(r, "null"); }

    /**
     * 解析数字，使其与 RawObject 的类型系统兼容：
     * - 整数优先返回 Integer（适配 asInt()），超出范围则返回 Long（适配 asLong()）
     * - 浮点数统一返回 Double（适配 asDouble()，asFloat() 可接受 Double）
     */
    private static Number parseNumber(StringReader r) {
        StringBuilder sb = new StringBuilder();
        while (r.hasNext() && (Character.isDigit(r.peek()) || r.peek() == '.' || r.peek() == '-' || r.peek() == '+' || r.peek() == 'e' || r.peek() == 'E')) {
            sb.append(r.next());
        }
        String num = sb.toString();
        if (num.contains(".") || num.contains("e") || num.contains("E")) {
            return Double.parseDouble(num);
        } else {
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                return Long.parseLong(num);
            }
        }
    }

    private static void consume(StringReader r, String expected) {
        for (char c : expected.toCharArray()) {
            if (!r.hasNext() || r.next() != c) throw new RuntimeException("Expected " + expected);
        }
    }

    // ---------- 内部字符流 ----------
    private static class StringReader {
        private final String str;
        private int pos;
        StringReader(String s) { this.str = s; this.pos = 0; }
        char peek() { return str.charAt(pos); }
        char next() { return str.charAt(pos++); }
        boolean hasNext() { return pos < str.length(); }
    }
}
