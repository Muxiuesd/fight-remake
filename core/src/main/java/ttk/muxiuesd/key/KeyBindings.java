package ttk.muxiuesd.key;

import com.badlogic.gdx.Input;
import ttk.muxiuesd.Fight;

/**
 * 游戏内所有的按键绑定
 * */
public class KeyBindings {
    /// 玩家移动键
    public static KeyBinding PlayerWalkUp = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_up"), "key_player_up",
        KeyBinding.Type.Keyboard, Input.Keys.W
    ));
    public static KeyBinding PlayerWalkDown = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_down"), "key_player_down",
        KeyBinding.Type.Keyboard, Input.Keys.S
    ));
    public static KeyBinding PlayerWalkLeft = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_left"), "key_player_left",
        KeyBinding.Type.Keyboard, Input.Keys.A
    ));
    public static KeyBinding PlayerWalkRight = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_right"), "key_player_right",
        KeyBinding.Type.Keyboard, Input.Keys.D
    ));
    /// 玩家操作键
    public static KeyBinding PlayerDropItem = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_drop_item"), "key_player_drop_item",
        KeyBinding.Type.Keyboard, Input.Keys.Q
    ));
    public static KeyBinding PlayerBackpackScreen = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_backpack_screen"), "key_player_backpack_screen",
        KeyBinding.Type.Keyboard, Input.Keys.E
    ));
    public static KeyBinding PlayerShoot = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shoot"), "key_player_shoot",
        KeyBinding.Type.Mouse, Input.Buttons.LEFT
    ));
    public static KeyBinding PlayerUseItem = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_use_item"), "key_player_use_item",
        KeyBinding.Type.Mouse, Input.Buttons.LEFT
    ));
    public static KeyBinding PlayerChangeItem = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_change_item"), "key_player_change_item",
        KeyBinding.Type.Mouse, Input.Buttons.MIDDLE
    ));
    public static KeyBinding PlayerShield = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shield"), "key_player_shield",
        KeyBinding.Type.Mouse, Input.Buttons.RIGHT
    ));
    public static KeyBinding PlayerInteract = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_interact"), "key_player_interact",
        KeyBinding.Type.Mouse, Input.Buttons.RIGHT
    ));
    public static KeyBinding PlayerShift = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shift"), "key_player_shift",
        KeyBinding.Type.Keyboard, Input.Keys.SHIFT_LEFT
    ));
    public static KeyBinding PlayerCameraZoomIn = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_camera_zoom_in"), "key_player_camera_zoom_in",
        KeyBinding.Type.Keyboard, Input.Keys.UP
    ));
    public static KeyBinding PlayerCameraZoomOut = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_camera_zoom_out"), "key_player_camera_zoom_out",
        KeyBinding.Type.Keyboard, Input.Keys.DOWN
    ));
    /// 玩家快捷键
    public static KeyBinding PlayerShortcutKey_1 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_1"), "key_player_shortcut_key_1",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_1
    ));
    public static KeyBinding PlayerShortcutKey_2 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_2"), "key_player_shortcut_key_2",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_2
    ));
    public static KeyBinding PlayerShortcutKey_3 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_3"), "key_player_shortcut_key_3",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_3
    ));
    public static KeyBinding PlayerShortcutKey_4 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_4"), "key_player_shortcut_key_4",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_4
    ));
    public static KeyBinding PlayerShortcutKey_5 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_5"), "key_player_shortcut_key_5",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_5
    ));
    public static KeyBinding PlayerShortcutKey_6 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_6"), "key_player_shortcut_key_6",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_6
    ));
    public static KeyBinding PlayerShortcutKey_7 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_7"), "key_player_shortcut_key_7",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_7
    ));
    public static KeyBinding PlayerShortcutKey_8 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_8"), "key_player_shortcut_key_8",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_8
    ));
    public static KeyBinding PlayerShortcutKey_9 = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_shortcut_key_9"), "key_player_shortcut_key_9",
        KeyBinding.Type.Keyboard, Input.Keys.NUM_9
    ));


    public static KeyBinding ExitGame = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_exit_game"), "key_exit_game",
        KeyBinding.Type.Keyboard, Input.Keys.ESCAPE
    ));

    public static KeyBinding ChunkBoundaryDisplay = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_chunk_boundary_display"), "key_chunk_boundary_display",
        KeyBinding.Type.Keyboard, Input.Keys.C
    ));
    public static KeyBinding HitboxDisplay = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_hitbox_display"), "key_hitbox_display",
        KeyBinding.Type.Keyboard, Input.Keys.H
    ));
    public static KeyBinding PlayerPositionPrint = InputBinding.registerBinding(new KeyBinding(
        Fight.ID("key_player_position_print"), "key_player_position_print",
        KeyBinding.Type.Keyboard, Input.Keys.P
    ));
}
