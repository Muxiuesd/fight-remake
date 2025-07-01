package ttk.muxiuesd.audio;

import ttk.muxiuesd.id.Identifier;

/**
 * 音效
 * */
public class Audio {
    private final String id;

    public Audio (String id){
        if (Identifier.check(id)) {
            this.id = id;
        }else {
            throw new IllegalArgumentException("id：" + id + " 不合法！！！");
        }
    }
    //TODO 更多操作


    public String getId () {
        return this.id;
    }
}
