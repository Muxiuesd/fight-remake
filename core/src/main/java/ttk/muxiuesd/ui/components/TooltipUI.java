package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品词条UI组件
 * <p>
 * 显示物品的名称、各种属性、耐久等等信息
 * */
public class TooltipUI extends UIComponent {
    public static final int FONT_SIZE = 16; //字体大小，最好是8的整数倍，不然中文字体会糊
    public static final float FONT_SCALE = 0.5f; //字体缩放，最好缩放后也是8的整数倍
    /// 上下左右边界大小
    public static final int LEFT = 2;
    public static final int RIGHT = 2;
    public static final int TOP = 2;
    public static final int BOTTOM = 2;

    //单例模式
    private static TooltipUI INSTANCE;

    public static TooltipUI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TooltipUI();
        }
        return INSTANCE;
    }
    public static void setInstance(TooltipUI instance) {
        if (instance != null) INSTANCE = instance;
    }

    //当前uiScreen
    public UIScreen curScreen;
    public SlotUI curSlotUI;

    /**
     * 激活词条UI
     * @param screen 需要基于哪个UI屏幕来激活，坐标会相对于那个面板
     * */
    public static TooltipUI activate (UIScreen screen, SlotUI slotUI) {

        TooltipUI instance = getInstance();
        //由鼠标坐标来给出基础坐标
        instance.setPosition(Util.getMouseUIPosition().add(1, 1));
        instance.curScreen = screen;
        screen.addComponent(instance);

        instance.curSlotUI = slotUI;
        instance.setCurItemStack(slotUI.getItemStack());

        return INSTANCE;
    }

    /**
     * 使词条UI失活
     * */
    public static TooltipUI deactivate () {
        return deactivate(getInstance().curSlotUI);
    }

    /**
     * 使词条UI失活，需要检查是不是对应物品槽位
     * */
    public static TooltipUI deactivate (SlotUI slotUI) {
        TooltipUI instance = getInstance();
        //在当前显示的UI屏幕上移除词条组件
        if (instance.curScreen != null) {
            instance.curScreen.removeComponent(instance);
            instance.curScreen = null;
        }
        if (instance.curSlotUI == slotUI) {
            instance.curSlotUI = null;
            instance.setCurItemStack(null);
        }

        return instance;
    }

    private NinePatch backgroundNinePatch;
    private NinePatch frameNinePatch;
    private ItemStack curItemStack;

    public TooltipUI () {
        super(1145f, 1145f, 100, 100, new GridPoint2(10, 10));
        this.backgroundNinePatch = this.createNinePatch(Util.loadTextureRegion(
                Fight.ID("tooltip_background"),
                Fight.UITexturePath("tooltip_background.png")
            ),
            LEFT, RIGHT, TOP, BOTTOM
        );
        this.frameNinePatch = this.createNinePatch(Util.loadTextureRegion(
                Fight.ID("tooltip_frame"),
                Fight.UITexturePath("tooltip_frame.png")
            ),
            LEFT, RIGHT, TOP, BOTTOM
        );

        setEnabled(false);
        setZIndex(1000000);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        ItemStack itemStack = this.getCurItemStack();
        if (itemStack == null) return;

        int trueSize = (int) (FONT_SIZE * FONT_SCALE);
        //基础坐标由激活时给出
        int renderX = (int) getX();
        int renderY = (int) getY();

        //计算词条总的宽度和高度
        int renderWidth = LEFT + RIGHT;
        int renderHeight = TOP + BOTTOM;
        Array<Text> textArray = itemStack.getTooltips();
        if (textArray.size > 0) {
            renderHeight += (trueSize + 3) * textArray.size;

            int maxLength = 0;
            //查找最大
            for (Text text : textArray) {
                maxLength = Math.max(maxLength, text.getLength());
            }
            renderWidth += maxLength * trueSize + 4;
        }

        //绘制背景和框架
        this.backgroundNinePatch.draw(batch, renderX, renderY, renderWidth, renderHeight);
        this.frameNinePatch.draw(batch, renderX + 1, renderY + 1, renderWidth - 2, renderHeight - 2);

        BitmapFont font = Fonts.MC.getFont(FONT_SIZE);
        font.getData().setScale(FONT_SCALE);
        //词条
        this.drawTooltips(batch,
            new Vector2(renderX, renderY + renderHeight),
            textArray,
            font,
            trueSize);
    }

    /**
     * 绘制词条文本
     * @param position 相对于词条背景的左上角的坐标
     * */
    public void drawTooltips (Batch batch, Vector2 position, Array<Text> textArray, BitmapFont bitmapFont, int fontSize) {
        //边界计算
        int leftEdge = LEFT * 2;
        int topEdge = TOP * 2;

        for (int index = 0; index < textArray.size; index++) {
            float renderX = position.x + leftEdge;
            float renderY = position.y - topEdge - index * (fontSize + 2);

            Text text = textArray.get(index);
            bitmapFont.draw(batch, text.getText(), renderX, renderY);
        }
    }

    public ItemStack getCurItemStack () {
        return this.curItemStack;
    }

    public TooltipUI setCurItemStack (ItemStack curItemStack) {
        this.curItemStack = curItemStack;
        return this;
    }

    /**
     * 创建点九
     * */
    private NinePatch createNinePatch(TextureRegion textureRegion, int left, int right, int top, int bottom) {
        return new NinePatch(textureRegion, left, right, top, bottom);
    }
}
