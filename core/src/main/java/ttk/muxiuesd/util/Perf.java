package ttk.muxiuesd.util;

import game.muxiuesd.bedrockcore.util.PerfRecorder;

/**
 * 性能记录
 * */
public class Perf {
    public static final PerfRecorder RECORDER = new PerfRecorder();

    public static void begin () {
        RECORDER.begin();
    }
    public static void end () {
        RECORDER.end();
    }

    public static void start (String name) {
        RECORDER.start(name);
    }
    public static void stop () {
        RECORDER.stop();
    }
}
