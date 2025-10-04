package ttk.muxiuesd.ui.text;

/**
 * 文本
 * */
public class Text {

    public static  Text of (String text) {
        return new Text().setText(text);
    }


    private String text = "Null Text";


    public int getLength() {
        return this.getText().length();
    }

    public String getText () {
        return this.text;
    }

    public Text setText (String text) {
        this.text = text;
        return this;
    }

}
