package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 单独一个状态效果的UI组件
 * <p>
 * 可以重复利用
 * */
public class EffectUI extends UIComponent {
    public static final int FONT_SIZE = 16; //字体大小，最好是8的整数倍，不然中文字体会糊
    public static final float FONT_SCALE = 0.5f; //字体缩放，最好缩放后也是8的整数倍
    public static float TRUE_FONT_SIZE = FONT_SIZE * FONT_SCALE; //真实渲染出来的字体大小

    /// 上下左右边界大小
    public static final int LEFT = 3;
    public static final int RIGHT = 3;
    public static final int TOP = 3;
    public static final int BOTTOM = 3;

    public static final float ICON_WIDTH = 18f;
    public static final float ICON_HEIGHT = 18f;

    private NinePatch background;

    private StatusEffect statusEffect;
    private StatusEffect.Data statusEffectData;
    private Text effectText;
    private Text effectDurationText = Text.ofText(Fight.ID("effect_duration"));

    public EffectUI () {
        this.background = new NinePatch(
            Util.loadTextureRegion(
                Fight.ID("effect_background"),
                Fight.UITexturePath("effect_background.png")
            ),
            LEFT, RIGHT, TOP, BOTTOM
        );
    }

    @Override
    public void update (float delta) {
        //更新状态的持续时间文本以及状态等级
        this.getEffectDurationText().set(0, (int)this.getEffectData().getDuration());
        this.getEffectText().set(0, this.getEffectData().getLevel());

        //计算背景的总宽度和高度
        int renderWidth = this.background.getTexture().getWidth();
        int renderHeight = this.background.getTexture().getHeight();

        if (this.effectText != null) {
            renderWidth += (int) Math.max(
                (this.effectText.getLength() * TRUE_FONT_SIZE),
                (this.effectDurationText.getLength() * TRUE_FONT_SIZE)
            );
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
        StatusEffect effect = this.getStatusEffect();

        //绘制背景
        this.background.draw(batch, renderX, renderY, getWidth(), getHeight());

        TextureRegion uiTexture = effect.getIcon();


        float iconRenderX = renderX + LEFT;
        float iconRenderY = renderY + BOTTOM;

        //绘制状态效果的图标
        batch.draw(uiTexture, iconRenderX, iconRenderY, 18f, 18f);

        BitmapFont font = Fonts.MC.getFont(FONT_SIZE);
        font.getData().setScale(FONT_SCALE);

        float textRenderX = iconRenderX + 2;
        float textRenderY = iconRenderY + 2;

        //绘制状态效果名称
        font.draw(batch, this.getEffectText().getText(),
            textRenderX + ICON_WIDTH,
            textRenderY + TRUE_FONT_SIZE * 2
        );

        //绘制剩余时间
        font.draw(batch, this.getEffectDurationText().getText(),
            textRenderX + ICON_WIDTH,
            textRenderY + TRUE_FONT_SIZE - 2
        );
    }

    public NinePatch getBackground () {
        return this.background;
    }

    public EffectUI setBackground (NinePatch background) {
        this.background = background;
        return this;
    }

    public StatusEffect getStatusEffect () {
        return this.statusEffect;
    }

    /**
     * 设置要被显示的状态效果
     * */
    public EffectUI setStatusEffect (StatusEffect statusEffect) {
        this.statusEffect = statusEffect;
        //根据状态效果的id来设置文本的key
        this.setEffectText(Text.ofEffect(statusEffect.getId()));
        return this;
    }

    public StatusEffect.Data getEffectData () {
        return this.statusEffectData;
    }

    /**
     * 设置状态效果的数据
     * */
    public EffectUI setEffectData (StatusEffect.Data data) {
        if (data != null) {
            this.statusEffectData = data;
        }
        return this;
    }

    public Text getEffectText () {
        return this.effectText;
    }

    public EffectUI setEffectText (Text effectText) {
        if (effectText != null) this.effectText = effectText;
        return this;
    }

    public Text getEffectDurationText () {
        return effectDurationText;
    }

    public EffectUI setEffectDurationText (Text effectDurationText) {
        if (effectDurationText != null) this.effectDurationText = effectDurationText;
        return this;
    }
}
