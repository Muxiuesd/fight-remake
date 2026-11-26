package ttk.muxiuesd.id;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.Codecable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 标识符，ID的包装类
 * <p>
 * 构造器私有，外部只能通过 {@link #of} 创建
 * */
public class Identifier implements Codecable<Identifier> {
    //正则表达式规则：第一部分是小写字母，第二部分是小写字母、数字或下划线
    public static final String REGEX = "^[a-z]+:[a-z0-9_]+$";

    public static final Codec<Identifier> CODEC = CodecBuilder.<Identifier>create()
        .paramField("id", Identifier::getID, Codec.STRING)
        .factory(Identifier::of);

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



    private final String id;

    private Identifier (String namespace, String name) {
        this(namespace + ":" + name);
    }

    private Identifier (String id) {
        checkAndThrow(id);
        this.id = id;
    }


    public String getID () {
        return this.id;
    }

    /**
     * 值语义：相同的 id 字符串视为同一个标识符
     * <p>
     * 保证 Identifier 可以作为 HashMap/ConcurrentHashMap 的键，
     * 不同实例但相同 id 可以互相查找到
     * */
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier that)) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode () {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public Codec<Identifier> getCodec () {
        return CODEC;
    }
}
