package ttk.muxiuesd.key;

import java.util.HashMap;

/**
 * 按键映射（按键行为绑定）
 * TODO 解决按键冲突、一个按键多个绑定
 * */
public class InputBinding {
    private static final HashMap<String, KeyBinding> keyBindingMap = new HashMap<>();
    private static final HashMap<String, KeyBinding> buttonBindingMap = new HashMap<>();

    private static final HashMap<Integer, KeyBinding> keyToBinding = new HashMap<>();
    private static final HashMap<Integer, KeyBinding> buttonToBinding = new HashMap<>();

    /**
     * 注册按键定义的行为
     * */
    public static KeyBinding registerBinding (KeyBinding keyBinding) {
        if (keyBinding.getType() == KeyBinding.Type.Keyboard) {
            keyBindingMap.put(keyBinding.id, keyBinding);
            keyToBinding.put(keyBinding.getKeyCode(), keyBinding);
            return keyBinding;
        }else if (keyBinding.getType() == KeyBinding.Type.Mouse) {
            buttonBindingMap.put(keyBinding.id, keyBinding);
            buttonToBinding.put(keyBinding.getKeyCode(), keyBinding);
            return keyBinding;
        }
        throw new IllegalArgumentException("不合法的按键类型：" + keyBinding.getType());
    }

    /**
     * 移动绑定
     * @param keycode 绑定到的新的键位
     * */
    public static void moveBinding (int keycode, KeyBinding keyBinding) {
        if (keyBinding.getType() == KeyBinding.Type.Keyboard) {
            keyToBinding.remove(keyBinding.getKeyCode());
            keyToBinding.put(keycode, keyBinding);
        }
        if (keyBinding.getType() == KeyBinding.Type.Mouse) {
            buttonToBinding.remove(keyBinding.getKeyCode());
            buttonToBinding.put(keycode, keyBinding);
        }
    }

    /**
     * 更新键盘按键状态
     * @param keyCode 按下的按键
     * @param pressed 按下则true，放开或根本没按则false
     * */
    public static void updateKeyState(int keyCode, boolean pressed) {
        if (!keyToBinding.containsKey(keyCode)) return;

        KeyBinding keyBinding = keyToBinding.get(keyCode);
        keyBinding.pressed = pressed;
    }

    /**
     * 更新鼠标按键状态
     * */
    public static void updateButtonState(int buttonCode, boolean pressed) {
        if (!buttonToBinding.containsKey(buttonCode)) return;

        KeyBinding keyBinding = buttonToBinding.get(buttonCode);
        keyBinding.pressed = pressed;
    }
}
