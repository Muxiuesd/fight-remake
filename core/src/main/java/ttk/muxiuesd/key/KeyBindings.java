package ttk.muxiuesd.key;

import com.badlogic.gdx.Input;
import ttk.muxiuesd.Fight;

/**
 * 游戏内所有的按键绑定
 * */
public class KeyBindings {
    public static KeyBinding PlayerWalkUp = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_up"), "key_player_up",
        KeyBinding.Type.Keyboard, Input.Keys.W
    ));
    public static KeyBinding PlayerWalkDown = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_down"), "key_player_down",
        KeyBinding.Type.Keyboard, Input.Keys.S
    ));
    public static KeyBinding PlayerWalkLeft = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_left"), "key_player_left",
        KeyBinding.Type.Keyboard, Input.Keys.A
    ));
    public static KeyBinding PlayerWalkRight = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_right"), "key_player_right",
        KeyBinding.Type.Keyboard, Input.Keys.D
    ));

    public static KeyBinding PlayerShoot = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_shoot"), "key_player_shoot",
        KeyBinding.Type.Mouse, Input.Buttons.LEFT
    ));
    public static KeyBinding PlayerShield = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_shield"), "key_player_shield",
        KeyBinding.Type.Mouse, Input.Buttons.RIGHT
    ));

}
