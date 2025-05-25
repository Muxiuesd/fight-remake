package ttk.muxiuesd.id;

import java.util.regex.Pattern;

/**
 * 标识符，ID的包装类
 * */
public class Identifier {
    // 正则表达式规则：第一部分是小写字母，第二部分是小写字母、数字或下划线
    public static final String REGEX = "^[a-z]+:[a-z0-9_]+$";
    /**
     * 检查输入的id是否合法
     * */
    public static boolean check (String input) {
        return Pattern.matches(REGEX, input);
    }

    private String id;

    public Identifier (String namespace, String name) {
        this(namespace + ":" + name);
    }

    public Identifier (String id) {
        this.setId(id);
    }

    public String getId () {
        return this.id;
    }

    public Identifier setId (String id) {
        if (!check(id)) {
            throw new IllegalArgumentException("输入的ID：" + id + " 不合法！！！");
        }
        this.id = id;
        return this;
    }
}
