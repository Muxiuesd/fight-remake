package ttk.muxiuesd.lang;

import com.badlogic.gdx.Gdx;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;

/**
 * 游戏的语言国际化系统
 * */
public class FI18N {
    public static void init () {
        Log.print(FI18N.class.getName(), "加载游戏语言配置文件……");
    }

    public static final LangPack ZH_CN = register("zh_cn", "中文");


    public static LangPack register (String namespace, String name) {
        //根据namespace加载语言文件
        return register(Fight.ID(namespace), name, Fight.LangPath(namespace));
    }

    /**
     * 注册一种语言包
     * */
    public static LangPack register (String id, String name, String langFilePath) {
        LangPack langPack = new LangPack(id, name);
        langPack.loadTexts(Gdx.files.internal(langFilePath));
        return Registries.LANG_HOLDER.register(new Identifier(id), langPack);
    }

    private static LangPack curLang;

    /**
     * 当前游戏的语言配置
     * */
    public static LangPack curLang () {
        if (curLang == null) {
            curLang = ZH_CN;
        }
        return curLang;
    }
}
