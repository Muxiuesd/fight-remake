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
     * 更换绑定
     * */
    public static void moveBinding (String bindingId, KeyBinding.Type newType, int newKeycode) {
        KeyBinding keyBinding = keyBindingMap.get(bindingId);
        KeyBinding.Type oldType;
        if (keyBinding != null) {
            oldType = keyBinding.getType();
        }else {
            keyBinding = buttonBindingMap.get(bindingId);
            if (keyBinding != null) {
                oldType = keyBinding.getType();
            }else {
                throw new IllegalArgumentException("按键绑定id：" + bindingId + " 不存在！！！");
            }
        }

        if (oldType == KeyBinding.Type.Keyboard) {
            //键盘
            keyToBinding.remove(keyBinding.getKeyCode());
        }else {
            //鼠标
            buttonToBinding.remove(keyBinding.getKeyCode());
        }

        moveBinding(newKeycode, keyBinding);
    }
    /**
     * 移动绑定
     * @param newKeycode 绑定到的新的键位
     * @param keyBinding 被更换的绑定物
     * */
    public static void moveBinding (int newKeycode, KeyBinding keyBinding) {
        if (keyBinding.getType() == KeyBinding.Type.Keyboard) {
            keyToBinding.remove(keyBinding.getKeyCode());
            keyToBinding.put(newKeycode, keyBinding);
        }
        if (keyBinding.getType() == KeyBinding.Type.Mouse) {
            buttonToBinding.remove(keyBinding.getKeyCode());
            buttonToBinding.put(newKeycode, keyBinding);
        }
    }

    /**
     * 更新键盘按键状态
     * @param keyCode 按下的按键
     * @param pressed 按下则true，放开或根本没按则false
     * */
    public static void updateKeyPressed (int keyCode, boolean pressed) {
        if (!keyToBinding.containsKey(keyCode)) return;

        KeyBinding keyBinding = keyToBinding.get(keyCode);
        keyBinding.setPressed(pressed);
    }

    /*public static void updateKeyReleased (int keyCode, boolean released) {
        for (KeyBinding keyBinding : keyToBinding.values()) {
            keyBinding.setReleased(false);
        }
        if (!keyToBinding.containsKey(keyCode)) return;

        KeyBinding keyBinding = keyToBinding.get(keyCode);
        keyBinding.setReleased(released);
    }

    public static void updateKeyTyped (int keyCode, boolean typed) {
        for (KeyBinding keyBinding : keyToBinding.values()) {
            keyBinding.setTyped(false);
        }
        if (!keyToBinding.containsKey(keyCode)) return;

        KeyBinding keyBinding = keyToBinding.get(keyCode);
        keyBinding.setTyped(typed);
    }*/

    /**
     * 更新鼠标按键状态
     * */
    public static void updateButtonState(int buttonCode, boolean pressed) {
        if (!buttonToBinding.containsKey(buttonCode)) return;

        KeyBinding keyBinding = buttonToBinding.get(buttonCode);
        keyBinding.setPressed(pressed);
    }
}
