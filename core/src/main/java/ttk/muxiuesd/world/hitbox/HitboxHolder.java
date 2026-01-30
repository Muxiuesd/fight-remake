package ttk.muxiuesd.world.hitbox;

import ttk.muxiuesd.interfaces.util.Voidable;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 碰撞箱持有类，存有并且管理多种、多个碰撞箱
 * */
public class HitboxHolder<T> {
    public static final VoidHitbox VOID_HITBOX = new VoidHitbox();
    public static final CollidedAction<?> VOID_ACTION = (holder, hitboxID, thisBox, otherBox) -> {};

    private T holder; //这个类的持有者
    private HashMap<String, Hitbox> boxes;
    private HashMap<String, CollidedAction<T>> actions;

    public HitboxHolder (T holder) {
        this(holder, new LinkedHashMap<>(), new LinkedHashMap<>());
    }
    public HitboxHolder(T holder, HashMap<String, Hitbox> boxesMap, HashMap<String, CollidedAction<T>> actionsMap) {
        this.holder = holder;
        this.boxes = boxesMap;
        this.actions = actionsMap;
    }

    /**
     * 核心方法：对每一个碰撞箱都检测一次碰撞事件
     * */
    public void handleCollision (HitboxHolder<?> otherHolder) {
        this.getBoxes().forEach((thisBoxID, thisBox) -> {
            otherHolder.getBoxes().forEach((otherBoxID, otherBox) -> {
                if (thisBox.checkCollision(otherBox)) {
                    //目前只处理当前这个持有者的碰撞事件，两个碰撞持有者会相互检测共两次（需要修改）
                    this.getAction(thisBoxID).handle(this.getHolder(), thisBoxID, thisBox, otherBox);
                }
            });
        });
    }

    public HitboxHolder<T> addBox (String id , Hitbox box) {
        return this.addBox(id, box, (CollidedAction<T>) VOID_ACTION);
    }
    /**
     * 添加一个碰撞箱
     * */
    public HitboxHolder<T> addBox (String id , Hitbox box, CollidedAction<T> action) {
        if (!this.getBoxes().containsKey(id) && !this.getBoxes().containsValue(box)) {
            this.getBoxes().put(id, box);
            this.setAction(id, action);
        }
        return this;
    }

    /**
     * 移除一个碰撞箱
     * */
    public HitboxHolder<T> removeBox (String id) {
        this.getBoxes().remove(id);
        return this;
    }

    /**
     * 根据id来获取碰撞箱
     * */
    public Hitbox getBox (String id) {
        Hitbox hitbox = this.getBoxes().get(id);
        if (hitbox == null && this.getHolder() != null) {
            Log.error(
                this.getClass().getName(),
                this.getHolder().getClass().getName() + "没有ID为：" + id + " 的碰撞箱！！！已用空对象代替！！！"
            );
            return VOID_HITBOX;
        }
        return hitbox;
    }

    /**
     * 添加一个碰撞事件
     * @param id 与碰撞箱的id对应，当这个id的碰撞箱发生碰撞，就调用这个id的碰撞事件
     * */
    public HitboxHolder<T> setAction (String id, CollidedAction<T> action) {
        if (!this.actions.containsKey(id) && !this.actions.containsValue(action)) {
            this.actions.put(id, action);
        }
        return this;
    }

    /**
     * 根据id来获取碰撞事件
     * */
    public CollidedAction<T> getAction (String id) {
        CollidedAction<T> action = this.actions.get(id);
        if (action == null) {
            Log.error(this.getClass().getName(), "ID为：" + id + " 的碰撞事件不存在！！！已用空对象代替！！！");
            return (CollidedAction<T>) VOID_ACTION;
        }
        return action;
    }

    public T getHolder () {
        return this.holder;
    }

    public HashMap<String, Hitbox> getBoxes () {
        return this.boxes;
    }

    public HashMap<String, CollidedAction<T>> getActions () {
        return this.actions;
    }

    /**
     * 碰撞事件接口
     * */
    @FunctionalInterface
    public interface CollidedAction<T> extends Voidable {
        void handle (T holder, String hitboxID, Hitbox thisBox, Hitbox otherBox);
    }
}
