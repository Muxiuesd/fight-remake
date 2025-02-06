package ttk.muxiuesd.mod;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * mod引擎工厂类
 * */
public class EngineFactory {
    public static ScriptEngine createEngine() {
        return new ScriptEngineManager().getEngineByName("nashorn");
    }
}
