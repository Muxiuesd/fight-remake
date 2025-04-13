package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;

public abstract class KeyInputEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((Integer) args[0]);
    }
    public abstract void callback (int key);
}
