package ttk.muxiuesd.key;

import com.badlogic.gdx.Gdx;

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

    private boolean pressed = false;
    private boolean released = false;
    private boolean typed = false;

    public KeyBinding (String id, String name, Type type, int keyCode) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.keyCode = keyCode;
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

    /**
     * 按下按键
     * */
    public boolean wasPressed() {
        return this.pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean wasJustPressed() {
        if (this.type == Type.Keyboard) return Gdx.input.isKeyJustPressed(keyCode);
        return Gdx.input.isButtonJustPressed(keyCode);
    }

    public Type getType () {
        return this.type;
    }

    public void setType (Type type) {
        if (this.type == type) return;
        this.type = type;

    }


}
