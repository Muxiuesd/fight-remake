package ttk.muxiuesd.key;

/**
 * 按键绑定，键盘或鼠标的按键
 * */
public class KeyBinding {
    enum Type{
        Keyboard, Mouse
    }

    public String id;
    public String name;

    private Type type;
    private int keyCode;

    public boolean pressed = false;

    public KeyBinding (String id, String name, Type type, int keyCode) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.keyCode = keyCode;
    }

    /**
     * 按下按键
     * */
    public boolean wasPressed() {
        return this.pressed;
    }

    public int getKeyCode () {
        return this.keyCode;
    }

    public void setKeyCode (int keyCode) {
        if (this.keyCode == keyCode) return;

        InputBinding.moveBinding(keyCode, this);
        this.keyCode = keyCode;
    }

    public Type getType () {
        return this.type;
    }

    public void setType (Type type) {
        if (this.type == type) return;
        this.type = type;

    }


}
