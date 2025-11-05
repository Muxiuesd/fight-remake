package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 单独一个状态效果UI组件
 * */
public class EffectUI extends UIComponent {
    public static final int FONT_SIZE = 16; //字体大小，最好是8的整数倍，不然中文字体会糊
    public static final float FONT_SCALE = 0.5f; //字体缩放，最好缩放后也是8的整数倍

    /// 上下左右边界大小
    public static final int LEFT = 3;
    public static final int RIGHT = 3;
    public static final int TOP = 3;
    public static final int BOTTOM = 3;

    private NinePatch background;

    private StatusEffect statusEffect;
    private Text effectName;


    public EffectUI () {
        this.background = new NinePatch(
            Util.loadTextureRegion(
                Fight.ID("tooltip_background"),
                Fight.UITexturePath("tooltip_background.png")
            ),
            LEFT, RIGHT, TOP, BOTTOM
        );
    }

    @Override
    public void update (float delta) {
        //计算背景的总宽度和高度

        int renderWidth = LEFT + RIGHT;
        int renderHeight = this.background.getTexture().getHeight();

        if (this.effectName != null) {
            renderWidth += (int) (this.effectName.getLength() * FONT_SIZE * FONT_SCALE);
        }

        setSize(renderWidth, renderHeight);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float renderX = getX();
        float renderY = getY();
        if (parent != null) {
            renderX += parent.getWidth();
            renderY += parent.getHeight();
        }



        //绘制背景
        this.background.draw(batch, renderX, renderY, getWidth(), getHeight());

        StatusEffect effect = this.getStatusEffect();
        TextureRegion uiTexture = effect.getIcon();

        //绘制状态效果的图标
        batch.draw(uiTexture, renderX + LEFT, renderY + BOTTOM, 18f, 18f);

        //绘制状态效果名称

        //绘制剩余时间

    }

    public StatusEffect getStatusEffect () {
        return this.statusEffect;
    }

    /**
     * 设置要被显示的状态效果
     * */
    public EffectUI setStatusEffect (StatusEffect statusEffect) {
        this.statusEffect = statusEffect;
        this.effectName = Text.ofEffect(statusEffect.getId());
        return this;
    }
}
