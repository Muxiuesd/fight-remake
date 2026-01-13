package ttk.muxiuesd.ui.text;

import com.badlogic.gdx.graphics.Color;
import ttk.muxiuesd.lang.FI18N;
import ttk.muxiuesd.lang.LangPack;

import java.util.HashMap;

/**
 * 游戏的文本类
 * <p>
 * 可以在文本里面插入格式化参数，使用格式：{<这里填入参数序号数字>}，例如："武器伤害： {0}"
 * */
public class Text {
    public static final String COLOR_MARK = "&&";   //颜色标记
    public static HashMap<Character, Color> COLOR_MAP = new HashMap<>();
    public static final int DEFAULT_ARGS = 10;
    static {
        COLOR_MAP.put('r', Color.RED);
        COLOR_MAP.put('g', Color.GREEN);
        COLOR_MAP.put('b', Color.BLUE);
        COLOR_MAP.put('y', Color.YELLOW);
        COLOR_MAP.put('c', Color.CYAN);
        COLOR_MAP.put('m', Color.MAGENTA);
        COLOR_MAP.put('o', Color.ORANGE);
        COLOR_MAP.put('p', Color.PINK);
        COLOR_MAP.put('w', Color.WHITE);
        COLOR_MAP.put('k', Color.BLACK);
        COLOR_MAP.put('l', Color.LIGHT_GRAY);
        COLOR_MAP.put('d', Color.DARK_GRAY);
        COLOR_MAP.put('a', Color.GRAY);
    }

    /**
     * 物品相关的文本
     * */
    public static Text ofItem (String itemId) {
        return new Text().add("item.").add(itemId).build();
    }

    /**
     * 纯文本
     * */
    public static Text ofText (String textId) {
        return new Text().add("text.").add(textId).build();
    }

    /**
     * 状态效果的文本
     * */
    public static Text ofEffect (String effectId) {
        return new Text().add("effect.").add(effectId).build();
    }

    /**
     * 最基础的文本
     * */
    public static Text of (String textKey) {
        return new Text().setKey(textKey);
    }

    private String textKey = "text.null_text";
    private StringBuilder stringBuilder;
    private Object[] args;   //文本的格式化参数

    public Text () {
        this.stringBuilder = new StringBuilder();
        this.args = new Object[DEFAULT_ARGS];
    }

    /**
     * 获取文本长度，返回文本参数格式化后的字符总数
     * */
    public int getLength() {
        return this.getText(this.getArgs()).length();
    }


    /**
     * 获取当前语言包的格式化过后的文本
     * */
    public String getText (Object[] args) {
        return this.getText(FI18N.curLang(), args);
    }

    /**
     * 使用当前游戏的语言包来获取翻译后的文本
     * */
    public String getText () {
        return this.getText(FI18N.curLang());
    }

    /**
     * 指定语言包来获取翻译文本
     * */
    public String getText (LangPack langPack) {
        return this.getText(langPack, this.getArgs());
    }

    /**
     * 基础核心方法：获取指定语言包的格式化过后的文本
     * @return 返回格式化过后的文本，依然带有颜色标记
     * */
    public String getText (LangPack langPack, Object[] args) {
        String result = langPack.getText(this.getKey());

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String placeholder = "{" + i + "}";
                String value = args[i] != null ? args[i].toString() : "NULL";
                result = result.replace(placeholder, value);
            }
        }

        return result;
    }

    /**
     * 添加键值
     * */
    public Text add (String str) {
        this.stringBuilder.append(str);
        return this;
    }

    /**
     * 构建
     * */
    public Text build () {
        this.setKey(this.stringBuilder.toString());
        this.clear();
        return this;
    }

    public Text clear () {
        this.stringBuilder.setLength(0);
        return this;
    }

    /**
     * 设置要格式化的位置的值
     * */
    public Text set (int index, Object object) {
        this.args[index] = object;
        return this;
    }

    public Text setKey (String textKey) {
        this.textKey = textKey;
        return this;
    }

    public String getKey () {
        return this.textKey;
    }

    public Object[] getArgs () {
        return this.args;
    }
}
