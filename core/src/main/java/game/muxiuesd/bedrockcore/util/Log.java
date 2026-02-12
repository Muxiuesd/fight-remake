package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.Gdx;

/**
 * 日志工具类
 * */
public class Log {
    public static void print(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    public static void error(String tag, String message) {
        Gdx.app.error(tag, message);
    }

    public static void error(String tag, String message, Throwable error) {
        Gdx.app.error(tag, message, error);
    }
}
