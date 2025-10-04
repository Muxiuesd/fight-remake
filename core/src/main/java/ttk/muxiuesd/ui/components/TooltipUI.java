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
import ttk.muxiuesd.ui.PlayerInventoryUIPanel;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.screen.PlayerInventoryScreen;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品词条UI组件
 * */
public class TooltipUI extends UIComponent {
    public static final int TOOLTIP_TEXT_FONT_SIZE = 7;
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

    /**
     * 激活词条UI
     * */
    public static TooltipUI activate () {
        PlayerInventoryUIPanel inventoryUIPanel = PlayerInventoryScreen.getInventoryUIPanel();
        TooltipUI instance = getInstance();
        //由鼠标坐标来给出基础坐标
        instance.setPosition(Util.getMouseUIPosition());
        inventoryUIPanel.addComponent(instance);

        return INSTANCE;
    }

    /**
     * 使词条UI失活
     * */
    public static TooltipUI deactivate () {
        TooltipUI instance = getInstance();
        PlayerInventoryUIPanel inventoryUIPanel = PlayerInventoryScreen.getInventoryUIPanel();
        inventoryUIPanel.removeComponent(instance);

        return instance;
    }

    private NinePatch backgroundNinePatch;
    private ItemStack curItemStack;

    public TooltipUI () {
        super(1145f, 1145f, 100, 100, new GridPoint2(10, 10));
        this.backgroundNinePatch = this.createNinePatch(Util.loadTextureRegion(
                Fight.ID("tooltip_background"),
                Fight.UITexturePath("tooltip_background.png")
            ),
            LEFT, RIGHT, TOP, BOTTOM
        );
    }

    /**
     * 创建点九
     * */
    private NinePatch createNinePatch(TextureRegion textureRegion, int left, int right, int top, int bottom) {
        return new NinePatch(textureRegion, left, right, top, bottom);
    }



    @Override
    public void draw (Batch batch, UIPanel parent) {
        ItemStack itemStack = this.getCurItemStack();
        //基础坐标由激活时给出
        int renderX = (int) getX();
        int renderY = (int) getY();
        //计算词条总的宽度和高度
        int renderWidth = LEFT + RIGHT;
        int renderHeight = TOP + BOTTOM;

        Array<Text> textArray = itemStack.getTooltips();
        if (textArray.size > 0) {
            renderHeight += (TOOLTIP_TEXT_FONT_SIZE + 3) * textArray.size;

            int maxLength = 0;
            for (Text text : textArray) {
                if (text.getLength() > maxLength) maxLength = text.getLength();
            }

            renderWidth += maxLength * TOOLTIP_TEXT_FONT_SIZE;
        }


        //绘制背景
        this.backgroundNinePatch.draw(batch, renderX, renderY, renderWidth, renderHeight);

        BitmapFont font = Fonts.MC.getFont(TOOLTIP_TEXT_FONT_SIZE);
        //词条
        this.drawTooltips(batch, new Vector2(renderX, renderY + renderHeight), textArray, font, TOOLTIP_TEXT_FONT_SIZE);
    }

    /**
     * 绘制词条文本
     * @param position 相对于词条背景的左上角的坐标
     * */
    public void drawTooltips (Batch batch, Vector2 position, Array<Text> textArray, BitmapFont bitmapFont, int fontSize) {
        for (int index = 0; index < textArray.size; index++) {
            Text text = textArray.get(index);
            bitmapFont.draw(batch, text.getText(), position.x + LEFT, position.y - TOP - index * fontSize);
        }
    }


    public ItemStack getCurItemStack () {
        return this.curItemStack;
    }

    public TooltipUI setCurItemStack (ItemStack curItemStack) {
        this.curItemStack = curItemStack;
        return this;
    }
}
