package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;

public abstract class ButtonInputEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
    }

    public abstract void callback (int screenX, int screenY, int pointer, int button);
}
