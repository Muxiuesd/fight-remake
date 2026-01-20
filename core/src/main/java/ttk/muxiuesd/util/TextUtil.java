package ttk.muxiuesd.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import ttk.muxiuesd.ui.text.FontHolder;
import ttk.muxiuesd.ui.text.Text;

/**
 * 文本相关的工具类
 * */
public class TextUtil {
    public static final GlyphLayout staticGlyphLayout = new GlyphLayout();
    public static float getTextRenderWidth (FontHolder fontHolder, int fontSize, String text) {
        return getTextRenderWidth(fontHolder.getFont(fontSize), text);
    }
    /**
     * 获取文本字体渲染的总宽度
     * @param text 传入的文本Sting，会自动转换成去掉颜色标记的纯文本
     * */
    public static float getTextRenderWidth (BitmapFont bitmapFont, String text) {
        TextUtil.staticGlyphLayout.setText(bitmapFont, getPlainText(text));
        return staticGlyphLayout.width;
    }
    /**
     * 渲染带颜色标记的文本
     * @param batch Batch 对象
     * @param font 字体
     * @param text 包含颜色标记的文本，如 "&&g绿色文本&&r红色文本"
     * @param x 起始x坐标
     * @param y 起始y坐标
     */
    public static void draw (Batch batch, BitmapFont font, String text, float x, float y) {
        draw(batch, font, text, x, y, Color.WHITE);
    }

    /**
     * 渲染带颜色标记的文本（支持默认颜色）
     * @param batch Batch 对象
     * @param font 字体
     * @param text 包含颜色标记的文本
     * @param x 起始x坐标
     * @param y 起始y坐标
     * @param defaultColor 默认颜色（当没有颜色标记时使用）
     */
    public static void draw (Batch batch, BitmapFont font, String text, float x, float y, Color defaultColor) {
        if (text == null || text.isEmpty()) return;

        Color currentColor = defaultColor;
        float currentX = x;

        int i = 0;
        int length = text.length();

        while (i < length) {
            // 检查是否是颜色标记
            if (i + 2 < length && text.charAt(i) == '&' && text.charAt(i + 1) == '&') {
                char colorChar = text.charAt(i + 2);
                // 如果是有效的颜色标记
                if (Text.COLOR_MAP.containsKey(colorChar)) {
                    currentColor = Text.COLOR_MAP.get(colorChar);
                    i += 3; // 跳过 &&x
                    continue;
                }
            }

            // 提取普通文本段
            int start = i;
            while (i < length) {
                if (i + 2 < length && text.charAt(i) == '&' && text.charAt(i + 1) == '&') {
                    char colorChar = text.charAt(i + 2);
                    if (Text.COLOR_MAP.containsKey(colorChar)) {
                        break; // 遇到新的颜色标记，停止
                    }
                }
                i++;
            }

            // 渲染当前文本段
            if (start < i) {
                String segment = text.substring(start, i);
                font.setColor(currentColor);
                font.draw(batch, segment, currentX, y);

                // 计算下一个文本段的x位置
                currentX += getTextRenderWidth(font, segment);
            }
        }
    }

    /**
     * 获取去掉颜色标记的纯文本
     * @param text 包含颜色标记的文本，如 "&&g绿色文本&&b红色文本"
     * @return 去掉所有颜色标记后的纯文本，如 "绿色文本红色文本"
     */
    public static String getPlainText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int length = text.length();
        int i = 0;

        while (i < length) {
            // 检查是否是颜色标记 "&&x"
            if (i + 2 < length &&
                text.charAt(i) == '&' &&
                text.charAt(i + 1) == '&') {

                char colorChar = text.charAt(i + 2);
                // 检查是否是有效的颜色字符（在colorMap中）
                if (Text.COLOR_MAP.containsKey(colorChar)) {
                    // 跳过颜色标记 "&&x"（3个字符）
                    i += 3;
                    continue;
                }
            }

            // 普通字符，添加到结果中
            result.append(text.charAt(i));
            i++;
        }

        return result.toString();
    }
}
