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

    /**
     * 获取文本长度
     * */
    public int getLength() {
        return this.getText().length();
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
        return langPack.getText(this.getKey());
    }

    public Text add (String str) {
        this.stringBuilder.append(str);
        return this;
    }

    public Text build () {
        this.setKey(this.stringBuilder.toString());
        this.clear();
        return this;
    }

    public Text clear () {
        this.stringBuilder.setLength(0);
        return this;
    }

    public Text setKey (String textKey) {
        this.textKey = textKey;
        return this;
    }

    public String getKey () {
        return this.textKey;
    }
}
