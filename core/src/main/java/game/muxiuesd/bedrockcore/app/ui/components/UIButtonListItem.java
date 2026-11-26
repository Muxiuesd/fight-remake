package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.muxiuesd.bedrockcore.app.interfaces.ui.UIListItem;
import game.muxiuesd.bedrockcore.font.FontHolder;
import ttk.muxiuesd.ui.components.FightUITextButton;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.TextUtil;

/**
 * 可以作为列表UI项目的按钮组件
 * */
public class UIButtonListItem extends FightUITextButton implements UIListItem {

    public UIButtonListItem (Text text, ClickEvent clickEvent) {
        super(text, clickEvent);
    }

    @Override
    public void update (UIList uiList, float delta) {
        super.update(delta);
    }

    @Override
    public void draw (Batch batch, UIList uiList, float itemX, float itemY) {
        float x = itemX;
        float y = itemY;

        //渲染按钮背景贴图
        if (isClicked() && this.getClickBackgroundPatch() != null) {
            this.getClickBackgroundPatch().draw(batch, x, y, getWidth(), getHeight());
        }else if (isMouseOver() && this.getMouseOverBackgroundPatch() != null) {
            this.getMouseOverBackgroundPatch().draw(batch, x, y, getWidth(), getHeight());
        }else if (this.getBackgroundPatch() != null) {
            this.getBackgroundPatch().draw(batch, x, y, getWidth(), getHeight());
        }

        //渲染字体
        if (getText() != null) {
            BitmapFont bitmapFont = this.getFontHolder().getFont(FontHolder.FONT_SIZE);
            bitmapFont.getData().setScale(FontHolder.FONT_SCALE);

            //让文本在按钮中央渲染
            int renderSize = this.getFontRenderSize(FontHolder.FONT_SIZE, FontHolder.FONT_SCALE);
            float renderWidth = TextUtil.getTextRenderWidth(bitmapFont, this.getText().getString());
            float renderX = x + (getWidth() - renderWidth) / 2;

            TextUtil.draw(batch, bitmapFont, this.getText().getString(), renderX, y + renderSize + DEFAULT_EDGE);

            //恢复共享字体的缩放，防止影响其他使用同一字体的组件
            bitmapFont.getData().setScale(1f);
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch, UIList uiList) {
        super.renderShape(batch);
    }

    @Override
    public float getItemWidth () {
        return getWidth();
    }

    @Override
    public float getItemHeight () {
        return getHeight();
    }
}
