package ttk.muxiuesd.registrant;

import java.util.HashMap;

/**
 * 实体，方块，墙，等等游戏元素的注册
 * 为之后的MOD做准备
 * */
public class Registrant<T> {
    //命名空间
    private final String nameSpace;
    private final Class<T> clazzType;
    private final HashMap<String, T> r;

    public Registrant(String nameSpace, Class<T> clazzType) {
        this.nameSpace = nameSpace;
        this.clazzType = clazzType;
        this.r = new HashMap<>();
    }

    public Registrant<T> register (String id, T clazz)  {
        String fullName = this.getFullName(id);
        if (r.containsKey(fullName)) {
            return null;
        }
        r.put(fullName, clazz);
        return this;
    }

    public T get(String id) {
        String fullName = this.getFullName(id);
        if (!r.containsKey(fullName)) {
            return null;
        }
        try {
            return this.newInstance(fullName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return 返回一个注册的元素的实例
     * */
    private T newInstance (String fullName) throws Exception{
        T t = r.get(fullName);
        Class<?> aClass = Class.forName(t.getClass().getName());
        return (T) aClass.getDeclaredConstructor().newInstance();
    }

    private String getFullName(String id) {
        return nameSpace + ":" + id;
    }

    public Class<T> getClazzType() {
        return this.clazzType;
    }
}
