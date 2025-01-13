package ttk.muxiuesd.util;

import com.badlogic.gdx.Gdx;

public class Log {
    public static void print(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    public static void error(String tag, String message) {
        Gdx.app.error(tag, message);
    }

    public static void printPosition(Class clazz, float x, float y) {
        print(clazz.getName(), Util.position2Sting(x, y));
    }
}
