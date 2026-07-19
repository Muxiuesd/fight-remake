package ttk.muxiuesd.id;

import java.util.regex.Pattern;

/**
 * 标识符，ID的包装类
 * */
public class Identifier {
    //正则表达式规则：第一部分是小写字母，第二部分是小写字母、数字或下划线
    public static final String REGEX = "^[a-z]+:[a-z0-9_]+$";

    public static Identifier of (String id) {
        return new Identifier(id);
    }

    public static Identifier of (String namespace, String name) {
        return new Identifier(namespace, name);
    }

    /**
     * 检查输入的id是否合法，不合法就抛出异常
     * */
    public static void checkAndThrow (String id) {
        if (!check(id)) {
            throw new IllegalArgumentException("输入的ID：" + id + " 不合法！！！");
        }
    }

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
        checkAndThrow(id);
        this.id = id;
    }

    public String getID () {
        return this.id;
    }

    public void setID (String id) {
        if (check(id)) this.id = id;
    }
}
