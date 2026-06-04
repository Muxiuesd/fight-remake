package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.util.ScissorUtil;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.util.TextUtil;

/**
 * 文本框
 * <p>
 * 大部分逻辑由AI编写
 * */
public class UITextField extends UIComponent {
    private StringBuilder textStringBuilder;//当前的文本构建
    private int maxLength;                  //最大字符数
    private String placeholder;             //占位文本
    private FontHolder fontHolder;          //字体持有
    private int fontSize;                   //字体的字号大小
    private Color fontColor;            //字体的渲染颜色

    /// 字体渲染的左右内边距（单位：米，相对于组件左下角）
    private float paddingLeft, paddingRight;

    /// 光标
    private float cursorBlinkTime   = 0.5f; //闪烁周期（秒）
    private float cursorBlinkTimer  = 0f;
    private boolean cursorVisible   = true;
    private int cursorIndex         = 0;    //光标位置（0 ~ text.length()）

    private int selectionStart = -1;        //-1表示无选中，否则为起始索引（可小于/大于cursorIndex）
    private boolean focused = false;        //状态

    private final GlyphLayout glyphLayout = TextUtil.staticGlyphLayout;     //辅助度量（避免频繁new）

    private NinePatch backgroundPatch;
    private NinePatch cursorPatch;


    public UITextField (float width, float height,
                        FontHolder fontHolder,
                        NinePatch backgroundPatch, NinePatch cursorPatch) {
        this(0, 0, width, height, fontHolder, backgroundPatch, cursorPatch);
    }
    public UITextField (float x, float y, float width, float height,
                        FontHolder fontHolder,
                        NinePatch backgroundPatch, NinePatch cursorPatch) {
        super(x, y, width, height, new GridPoint2((int) width, (int) height));

        this.fontHolder = fontHolder;
        this.fontSize = FontHolder.FONT_SIZE;
        this.textStringBuilder = new StringBuilder();
        this.maxLength = 100;
        this.placeholder = "";
        this.fontColor = Color.WHITE;
        // 默认内边距（组件内文本区域）
        this.paddingLeft = 4f;
        this.paddingRight = 4f;

        this.backgroundPatch = backgroundPatch;
        this.cursorPatch = cursorPatch;
    }



    @Override
    public void update(float delta) {
        super.update(delta);
        //被选中的状态下，文本框的光标闪烁
        if (this.focused && isEnabled()) {
            this.cursorBlinkTimer += delta;
            if (this.cursorBlinkTimer >= this.cursorBlinkTime) {
                this.cursorBlinkTimer = 0;
                this.cursorVisible = !this.cursorVisible;
            }
        } else {
            this.cursorVisible = false;
        }
    }

    @Override
    public void draw(Batch batch, UIPanel parent) {
        if (!isVisible()) return;

        float x = getX(parent);
        float y = getY(parent);

        boolean hasBackground = this.backgroundPatch != null;
        /// 绘制背景
        if (hasBackground) {
            this.backgroundPatch.draw(batch, x, y, getWidth(), getHeight());
        }

        /// 文本的绘制
        //最终显示出来的文本
        String displayText = this.textStringBuilder.isEmpty() ? this.placeholder : this.textStringBuilder.toString();
        BitmapFont bitmapFont = this.getFont();
        bitmapFont.getData().setScale(FontHolder.FONT_SCALE);
        float renderHeight = TextUtil.getTextRenderHeight(bitmapFont, displayText);
        float renderX = x + this.paddingLeft;   //基于左边
        float renderY = y + renderHeight;
        if (hasBackground) renderY += this.backgroundPatch.getPadBottom();

        //裁剪字体渲染避免超出区域
        ScissorUtil.beginScissor(
            batch, GUICamera.INSTANCE.getCamera(),
            x + this.paddingLeft, y,
            getWidth() - this.paddingLeft - this.paddingRight, getHeight()
        );

        TextUtil.draw(batch, bitmapFont, displayText, renderX, renderY, this.getFontColor());

        /// 绘制光标（仅聚焦且可见）
        if (this.focused && this.cursorVisible) {
            float cursorX = renderX + getTextWidthBeforeIndex(this.cursorIndex);
            float cursorY = hasBackground ? y + this.backgroundPatch.getPadBottom() : y;
            float width   = 2f;
            float height  = renderHeight + 2f;
            this.cursorPatch.draw(batch, cursorX, cursorY, width, height);
        }
        //结束裁剪
        ScissorUtil.endScissor(batch);
    }

    /**
     * 绘制形状：背景、边框、选中背景、光标
     * */
    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        if (!isVisible()) return;

        float absX = getAbsX();
        float absY = getAbsY();
        float w = getWidth();
        float h = getHeight();
        //背景（填充整个组件
        /*shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(isEnabled() ? backgroundColor : disabledColor);
        shapeRenderer.rect(absX, absY, w, h);*/
        /*//边框（沿组件边缘）
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        if (focused) {
            shapeRenderer.setColor(focusedBorderColor);
        } else if (isMouseOver() && isEnabled()) {
            shapeRenderer.setColor(hoverBorderColor);
        } else {
            shapeRenderer.setColor(borderColor);
        }
        shapeRenderer.rect(absX, absY, w, h);

        //选中背景（仅当有选中且聚焦时，绘制在内边距区域内）
        if (focused && selectionStart != -1 && selectionStart != cursorIndex) {
            int start = Math.min(selectionStart, cursorIndex);
            int end   = Math.max(selectionStart, cursorIndex);
            String selectedStr = textStringBuilder.substring(start, end);
            glyphLayout.setText(this.getFont(), selectedStr);
            float selWidth = glyphLayout.width;

            float selX = absX + paddingLeft + getTextWidthBeforeIndex(start);
            float selY = absY + paddingBottom; // 内边距底部开始
            float selHeight = h - paddingTop - paddingBottom;

            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(selectionColor);
            shapeRenderer.rect(selX, selY, selWidth, selHeight);
        }*/

        //光标（仅聚焦且可见）
        /*if (this.focused && this.cursorVisible) {
            float cursorX = absX + this.paddingLeft + getTextWidthBeforeIndex(this.cursorIndex);
            float cursorY = absY + this.paddingBottom; // 底部内边距
            float cursorHeight = h - this.paddingTop - this.paddingBottom; // 内边距区域内高度
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(this.fontColor);
            shapeRenderer.rect(cursorX, cursorY, 1f, cursorHeight);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        }*/


        shapeRenderer.rect(this.getAbsX(), this.getAbsY(), this.getWidth(), this.getHeight());
    }

    // 鼠标点击：获得焦点，定位光标（交互网格左下角为原点）
    @Override
    public boolean click(GridPoint2 interactPos) {
        if (!isEnabled() || !isVisible()) return false;

        //让ui屏幕设置这个组件是焦点
        getScreen().setFocusComponent(this);

        this.focused = true;
        this.cursorBlinkTimer = 0;
        this.cursorVisible = true;

        //将网格索引转换为组件内坐标（左下角为原点）
        GridPoint2 gridSize = getInteractGridSize();
        float cellW = getWidth()  / gridSize.x;
        float cellH = getHeight() / gridSize.y;
        //取网格中心点作为点击位置
        float relativeX = (interactPos.x + 0.5f) * cellW;
        //Y坐标对于光标定位无直接影响，此处未使用

        setCursorFromRelativeX(relativeX);
        this.selectionStart = -1; // 点击清除选中

        return false;
    }

    /**
     * 根据相对于组件左边界的距离设置光标索引
     * @param relativeX 距离组件左边缘的距离（0 ~ width）
     */
    private void setCursorFromRelativeX(float relativeX) {
        float clickX = relativeX - paddingLeft;
        if (clickX <= 0) {
            cursorIndex = 0;
            return;
        }
        float accumulated = 0f;
        for (int i = 0; i < textStringBuilder.length(); i++) {
            TextUtil.staticGlyphLayout.setText(this.getFont(), String.valueOf(textStringBuilder.charAt(i)));
            float charWidth = TextUtil.staticGlyphLayout.width;
            if (accumulated + charWidth / 2f >= clickX) {
                cursorIndex = i;
                return;
            }
            accumulated += charWidth;
        }
        cursorIndex = textStringBuilder.length(); // 点击在末尾
    }

    // -------------------------------------------------------------------------
    // 键盘事件（需由外层焦点管理器调用）
    // -------------------------------------------------------------------------
    /**
     * 输入字符（最终提交）—— 完美支持中文、英文等
     */
    @Override
    public boolean keyTyped(char character) {
        if (!this.focused || !isEnabled()) return false;
        // 控制字符由 keyDown 处理
        if (character == '\b' || character == '\t' || character == '\n' ||
            character == '\r' || character == 127) return false;

        if (this.textStringBuilder.length() >= this.maxLength) return false;

        // 若存在选中，先删除选中文本
        if (this.selectionStart != -1 &&    this.selectionStart != this.cursorIndex) {
            deleteSelection();
        }

        this.textStringBuilder.insert(this.cursorIndex, character);
        this.cursorIndex++;
        this.selectionStart = -1;
        return false;
    }

    /** 按键处理（方向键、退格、删除、Home/End、回车、ESC） */
    @Override
    public boolean keyDown(int keycode) {
        if (!this.focused || !isEnabled()) return false;

        switch (keycode) {
            case Input.Keys.BACKSPACE:
                if (selectionStart != -1 && selectionStart != cursorIndex) {
                    deleteSelection();
                } else if (cursorIndex > 0) {
                    textStringBuilder.deleteCharAt(cursorIndex - 1);
                    cursorIndex--;
                }
                break;
            case Input.Keys.FORWARD_DEL:
                if (selectionStart != -1 && selectionStart != cursorIndex) {
                    deleteSelection();
                } else if (cursorIndex < textStringBuilder.length()) {
                    textStringBuilder.deleteCharAt(cursorIndex);
                }
                break;
            case Input.Keys.LEFT:
                if (cursorIndex > 0) {
                    selectionStart = -1; // 移动时清除选中（若需扩展选中，可添加Shift判定）
                    cursorIndex--;
                }
                break;
            case Input.Keys.RIGHT:
                if (cursorIndex < textStringBuilder.length()) {
                    selectionStart = -1;
                    cursorIndex++;
                }
                break;
            case Input.Keys.HOME:
                cursorIndex = 0;
                selectionStart = -1;
                break;
            case Input.Keys.END:
                cursorIndex = textStringBuilder.length();
                selectionStart = -1;
                break;

            case Input.Keys.ENTER:
            case Input.Keys.ESCAPE:
                //失焦
                this.focused = false;
                break;

            default:
                return false;
        }
        return true;
    }

    /**
     * 删除当前选中的文本，并将光标置于原选中起始位置
     * */
    private void deleteSelection () {
        int start = Math.min(selectionStart, cursorIndex);
        int end   = Math.max(selectionStart, cursorIndex);
        textStringBuilder.delete(start, end);
        cursorIndex = start;
        selectionStart = -1;
    }

    /**
     * 获取指定索引前的文本宽度（用于光标定位）
     * */
    private float getTextWidthBeforeIndex (int index) {
        if (index <= 0) return 0f;

        String before = textStringBuilder.substring(0, index);
        return TextUtil.getTextRenderWidth(this.getFont(), before);
    }

    public void setTextStringBuilder (String newText) {
        this.textStringBuilder.setLength(0);
        this.textStringBuilder.append(newText);
        this.cursorIndex = textStringBuilder.length();
        this.selectionStart = -1;
    }

    public String getTextStringBuilder () {
        return this.textStringBuilder.toString();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    // 样式设置方法（链式调用）
    public UITextField setPadding (float left, float right) {
        this.paddingLeft = left;
        this.paddingRight = right;
        return this;
    }

    public UITextField setFontColor(Color color) {
        this.fontColor = color;
        return this;
    }

    public Color getFontColor () {
        return this.fontColor;
    }

    public FontHolder getFontHolder () {
        return this.fontHolder;
    }

    public UITextField setFontHolder (FontHolder fontHolder) {
        this.fontHolder = fontHolder;
        return this;
    }

    public int getFontSize () {
        return this.fontSize;
    }

    public UITextField setFontSize (int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * 获取BitmapFont
     * */
    public BitmapFont getFont () {
        return this.getFontHolder().getFont(this.getFontSize());
    }
}
