package ttk.muxiuesd.ui.text;

import ttk.muxiuesd.lang.FI18N;
import ttk.muxiuesd.lang.LangPack;

/**
 * 文本
 * */
public class Text {

    /**
     * 物品
     * */
    public static Text ofItem (String itemId) {
        return new Text().add("item.").add(itemId).build();
    }

    public static Text ofText (String textId) {
        return new Text().add("text.").add(textId).build();
    }

    /**
     * 最基础的
     * */
    public static Text of (String textKey) {
        return new Text().setKey(textKey);
    }

    private StringBuilder stringBuilder = new StringBuilder();
    private String textKey = "text.null_text";
    private Object[] args = new Object[20];


    /**
     * 获取文本长度
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
     * 获取翻译后的文本
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
     * 基础：获取指定语言包的格式化过后的文本
     * */
    public String getText (LangPack langPack, Object[] args) {
        String result = langPack.getText(this.getKey());

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String placeholder = "{" + i + "}";
                String value = args[i] != null ? args[i].toString() : "null";
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
