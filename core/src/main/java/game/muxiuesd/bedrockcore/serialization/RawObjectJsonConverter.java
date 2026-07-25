package game.muxiuesd.bedrockcore.serialization;

import com.badlogic.gdx.utils.JsonWriter;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;

import java.util.List;
import java.util.Map;

/**
 * 将RawObject转化成json的工具类
 * */
public class RawObjectJsonConverter {
    /**
     * 将 RawObject 转换为 JSON 字符串
     */
    public static String toJson(RawObject raw) {
        JsonDataWriter writer = new JsonDataWriter();
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
}
