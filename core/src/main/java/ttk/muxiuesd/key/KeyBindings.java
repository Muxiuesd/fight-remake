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
    public static KeyBinding PlayerDropItem = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_drop_item"), "key_player_drop_item",
        KeyBinding.Type.Keyboard, Input.Keys.Q
    ));

    public static KeyBinding PlayerShoot = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_shoot"), "key_player_shoot",
        KeyBinding.Type.Mouse, Input.Buttons.LEFT
    ));
    public static KeyBinding PlayerUseItem = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_use_item"), "key_player_use_item",
        KeyBinding.Type.Mouse, Input.Buttons.LEFT
    ));
    public static KeyBinding PlayerChangeItem = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_change_item"), "key_player_change_item",
        KeyBinding.Type.Mouse, Input.Buttons.MIDDLE
    ));
    public static KeyBinding PlayerShield = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_shield"), "key_player_shield",
        KeyBinding.Type.Mouse, Input.Buttons.RIGHT
    ));
    public static KeyBinding PlayerInteract = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_interact"), "key_player_interact",
        KeyBinding.Type.Mouse, Input.Buttons.RIGHT
    ));
    public static KeyBinding PlayerShift = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_shift"), "key_player_shift",
        KeyBinding.Type.Keyboard, Input.Keys.SHIFT_LEFT
    ));
    public static KeyBinding PlayerCameraZoomIn = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_camera_zoom_in"), "key_player_camera_zoom_in",
        KeyBinding.Type.Keyboard, Input.Keys.UP
    ));
    public static KeyBinding PlayerCameraZoomOut = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_camera_zoom_out"), "key_player_camera_zoom_out",
        KeyBinding.Type.Keyboard, Input.Keys.DOWN
    ));

    public static KeyBinding ExitGame = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_exit_game"), "key_exit_game",
        KeyBinding.Type.Keyboard, Input.Keys.ESCAPE
    ));

    public static KeyBinding ChunkBoundaryDisplay = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_chunk_boundary_display"), "key_chunk_boundary_display",
        KeyBinding.Type.Keyboard, Input.Keys.C
    ));
    public static KeyBinding WallHitboxDisplay = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_wall_hitbox_display"), "key_wall_hitbox_display",
        KeyBinding.Type.Keyboard, Input.Keys.H
    ));
    public static KeyBinding PlayerPositionPrint = InputBinding.registerBinding(new KeyBinding(
        Fight.getId("key_player_position_print"), "key_player_position_print",
        KeyBinding.Type.Keyboard, Input.Keys.P
    ));
}
