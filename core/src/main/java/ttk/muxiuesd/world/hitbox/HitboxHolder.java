package ttk.muxiuesd.world.hitbox;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 碰撞箱持有类，存有并且管理多种、多个碰撞箱
 * */
public class HitboxHolder<T> {
    private T holder; //这个类的持有者
    private HashMap<String, Hitbox> boxes;
    private HashMap<String, CollidedAction> actions;

    public HitboxHolder () {
        this(new LinkedHashMap<>(), new LinkedHashMap<>());
    }
    public HitboxHolder(HashMap<String, Hitbox> boxesMap, HashMap<String, CollidedAction> actionsMap) {
        this.boxes = boxesMap;
        this.actions = actionsMap;
    }

    /**
     * 处理每一个碰撞事件
     * */
    public void handleCollision (HitboxHolder<?> otherHolder) {
        this.getBoxes().forEach((thisBoxID, thisBox) -> {
            otherHolder.getBoxes().forEach((otherBoxID, otherBox) -> {
                if (thisBox.checkCollision(otherBox)) {
                    this.getAction(thisBoxID).handle(this.getHolder(), thisBoxID, thisBox, otherBox);
                }
            });
        });
    }

    /**
     * 添加一个碰撞箱
     * */
    public HitboxHolder add (String id ,Hitbox box, CollidedAction action) {
        if (!this.boxes.containsKey(id) && !this.boxes.containsValue(box)) {
            this.boxes.put(id, box);
            this.setAction(id, action);
        }
        return this;
    }

    /**
     * 移除一个碰撞箱
     * */
    public HitboxHolder remove (String id) {
        this.boxes.remove(id);
        return this;
    }

    public HitboxHolder setAction (String id, CollidedAction action) {
        if (!this.actions.containsKey(id) && !this.actions.containsValue(action)) {
            this.actions.put(id, action);
        }
        return this;
    }

    public CollidedAction getAction (String id) {
        return this.actions.get(id);
    }

    public T getHolder () {
        return this.holder;
    }

    public HashMap<String, Hitbox> getBoxes () {
        return this.boxes;
    }

    public HashMap<String, CollidedAction> getActions () {
        return this.actions;
    }

    /**
     * 碰撞事件接口
     * */
    public interface CollidedAction<T> {
        /**
         *
         * */
        void handle (T holder, String hitboxID, Hitbox thisBox, Hitbox otherBox);
    }
}
