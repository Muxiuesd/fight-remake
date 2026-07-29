package ttk.muxiuesd.id;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.Codecable;

import java.util.regex.Pattern;

/**
 * 标识符，ID的包装类
 * */
public class Identifier implements Codecable<Identifier> {
    //正则表达式规则：第一部分是小写字母，第二部分是小写字母、数字或下划线
    public static final String REGEX = "^[a-z]+:[a-z0-9_]+$";

    public static final Codec<Identifier> CODEC = CodecBuilder.<Identifier>create()
        .field("id", Identifier::getID, Identifier::setID, Codec.STRING)
        .noArgFactory(Identifier::new);

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

    /**
     * 用于编解码的空参
     * */
    public Identifier () {}
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

    @Override
    public Codec<Identifier> getCodec () {
        return CODEC;
    }
}
