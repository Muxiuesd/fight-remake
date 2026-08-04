package ttk.muxiuesd.lang;

import com.badlogic.gdx.Gdx;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;

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
        return register(Identifier.of(Fight.ID(namespace)), name, Fight.LangPath(namespace));
    }

    /**
     * 注册一种语言包
     * */
    public static LangPack register (Identifier identifier, String name, String langFilePath) {
        LangPack langPack = new LangPack(identifier, name);
        langPack.loadTexts(Gdx.files.internal(langFilePath));
        return Registries.LANG_HOLDER.register(langPack.getIdentifier(), langPack);
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
