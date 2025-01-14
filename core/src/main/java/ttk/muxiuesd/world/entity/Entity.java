package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.Updateable;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.EntitySystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体
 */
public abstract class Entity implements Disposable, Drawable, Updateable {

    private static int idIndex = 0;

    public final int id;
    public Group group;
    public float maxHealth; // 生命值上限
    public float curHealth; // 当前生命值
    public float speed, curSpeed;

    public float x, y;
    public float width, height;
    public float originX, originY;
    public float scaleX = 1, scaleY = 1;
    public float rotation;
    public TextureRegion textureRegion;
    public Rectangle hurtbox = new Rectangle();

    public boolean attacked = false;

    private EntitySystem es;

    // 动态数据存储
    private Component[] dynamicData = new Component[10]; // 初始大小为10，可以根据需要调整
    private static Map<Class<? extends Component>, Integer> componentIndexMap = new HashMap<>();

    {
        this.id = idIndex++;
    }

    public void initialize(Group group, float maxHealth, float curHealth) {
        this.group = group;
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
    }

    public void draw(Batch batch) {
        if (this.attacked) {
            // 受到攻击变红
            batch.setColor(255, 0, 0, 255);
        }
        if (textureRegion != null) {
            batch.draw(textureRegion, x, y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
        // 还原batch
        batch.setColor(255, 255, 255, 255);
        attacked = false;
    }

    public void update(float delta) {
        this.setCullingArea(x, y, width, height);
    }

    public void setCullingArea(float x, float y, float width, float height) {
        this.hurtbox.set(x, y, width, height);
    }

    public void dispose() {
        if (this.textureRegion != null) {
            this.textureRegion = null;
        }
    }

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    /**
     * Sets the position of the actor's bottom left corner.
     */
    public void setPosition(float x, float y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Sets the width and height.
     */
    public void setSize(float width, float height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Set bounds the x, y, width, and height.
     */
    public void setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public Vector2 getPosition() {
        return new Vector2(this.x, this.y);
    }

    public void setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    // 添加组件
    public void addComponent(Component component) {
        Class<? extends Component> clazz = component.getClass();
        Integer index = componentIndexMap.get(clazz);
        if (index == null) {
            index = componentIndexMap.size();
            componentIndexMap.put(clazz, index);
            ensureCapacity(index);
        }
        dynamicData[index] = component;
    }

    // 获取组件
    public <T extends Component> T getComponent(Class<T> clazz) {
        Integer index = componentIndexMap.get(clazz);
        if (index != null && index < dynamicData.length) {
            return clazz.cast(dynamicData[index]);
        }
        return null; // 或者抛出异常
    }

    // 确保数组容量足够
    private void ensureCapacity(int index) {
        if (index >= dynamicData.length) {
            dynamicData = Arrays.copyOf(dynamicData, index + 1);
        }
    }

    public boolean isDeath () {
        return curHealth <= 0;
    }

    public void setEntitySystem(EntitySystem es) {
        this.es = es;
    }

    public EntitySystem getEntitySystem() {
        return this.es;
    }
}
