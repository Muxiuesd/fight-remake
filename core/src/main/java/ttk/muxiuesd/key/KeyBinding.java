package ttk.muxiuesd.key;

/**
 * 按键绑定，键盘或鼠标的按键
 * */
public class KeyBinding {
    public enum Type{
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

    /**
     * 更换绑定的按键
     * */
    public void changeKeyCode (Type newType, int newKeyCode) {
        if (this.keyCode == newKeyCode) return;

        InputBinding.moveBinding(this.id, newType, newKeyCode);
        this.keyCode = newKeyCode;
    }

    public Type getType () {
        return this.type;
    }

    public void setType (Type type) {
        if (this.type == type) return;
        this.type = type;

    }


}
