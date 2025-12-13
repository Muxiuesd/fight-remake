package ttk.muxiuesd.system.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import ttk.muxiuesd.system.abs.GameSystem;

/**
 * 游戏最底层的输入系统
 * */
public class InputHandleSystem extends GameSystem {
    private final InputMultiplexer inputMultiplexer;

    private InputHandleSystem() {
        this.inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(this.inputMultiplexer);
    }
    private static InputHandleSystem INSTANCE;
    public static InputHandleSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InputHandleSystem();
        }
        return INSTANCE;
    }

    public void addProcessor (InputProcessor processor) {
        this.inputMultiplexer.addProcessor(processor);
    }
}
