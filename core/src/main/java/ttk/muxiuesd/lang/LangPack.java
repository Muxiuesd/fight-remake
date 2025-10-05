package ttk.muxiuesd.lang;

import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * 语言包：持有一种语言的类
 * */
public class LangPack {
    private final String id; //语言id
    private final String name; //语言的名字

    private final HashMap<String, String> texts;    //文本映射


    public LangPack (String id, String name) {
        this.id = id;
        this.name = name;
        this.texts = new HashMap<>();
    }

    /**
     * 加载文本翻译
     * */
    public void loadTexts (FileHandle fileHandle) {
        if (!fileHandle.exists()) return;

        JsonDataReader jsonDataReader = new JsonDataReader(fileHandle.readString());
        jsonDataReader.getParse().forEach(textValue -> {
            String key = textValue.name();
            this.setText(key, jsonDataReader.readString(key));
        });

        Log.print(this.getClass().getName(), "语言：" + this.getName() + " 的文本配置文件加载完毕！");
    }

    /**
     * 获取文本
     * */
    public String getText (String textKey) {
        //如果获取不到翻译，就返回键作为文本
        String text = this.getTextsMap().get(textKey);
        return text == null ? textKey : text;
    }

    /**
     * 设置文本
     * */
    public void setText (String textKey, String text) {
        this.getTextsMap().put(textKey, text);
    }

    /**
     * 获取文本映射
     * */
    public HashMap<String, String> getTextsMap () {
        return this.texts;
    }

    public String getId () {
        return this.id;
    }

    public String getName () {
        return this.name;
    }
}
