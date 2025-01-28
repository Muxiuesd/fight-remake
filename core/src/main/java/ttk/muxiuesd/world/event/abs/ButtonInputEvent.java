package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;

public abstract class ButtonInputEvent implements Event {
    public abstract void call (int button);
}
