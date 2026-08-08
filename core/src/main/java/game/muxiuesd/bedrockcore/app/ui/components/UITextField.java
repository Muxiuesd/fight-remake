package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.util.ScissorUtil;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.util.TextUtil;
import ttk.muxiuesd.util.Util;

/**
 * 文本框
 * <p>
 * 大部分逻辑由AI编写
 * */
public class UITextField extends UIComponent {
    private StringBuilder textStringBuilder;//当前的文本构建者
    private int maxLength;                  //最大字符数
    private String tipText;                 //提示文本
    private FontHolder fontHolder;          //字体持有
    private int fontSize;                   //字体的字号大小（并不是最终的渲染大小）
    private Color textColor;                //输入文本字体的渲染颜色
    private Color tipTextColor;             //提示文本字体的渲染颜色

    /// 字体渲染的左右内边距（单位：米，相对于组件左下角）
    private float paddingLeft, paddingRight;

    /// 光标的一些属性
    private float cursorBlinkTime   = 0.5f; //闪烁周期（秒）
    private float cursorBlinkTimer  = 0f;
    private boolean cursorVisible   = true;
    private int cursorIndex         = 0;    //光标位置（0 ~ text.length()）

    private int selectionStart = -1;        //-1表示无选中，否则为起始索引（可小于/大于cursorIndex）

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
        this.tipText = "请输入文本";
        this.textColor = Color.WHITE;
        this.tipTextColor = Color.YELLOW;

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
        if (isFocused() && isEnabled()) {
            this.cursorBlinkTimer += delta;
            if (this.cursorBlinkTimer >= this.cursorBlinkTime) {
                this.cursorBlinkTimer = 0;
                this.cursorVisible = !this.cursorVisible;
            }
        } else {
            this.cursorVisible = false;
        }
    }

    /**
     * 文本框UI组件的核心渲染方法
     * */
    @Override
    public void draw(Batch batch, UIPanel parent) {
        if (!isVisible()) return;

        float x = getX(parent);
        float y = getY(parent);
        NinePatch background = this.getBackgroundPatch();
        boolean hasBackground = background != null;
        /// 绘制背景
        if (hasBackground) {
            background.draw(batch, x, y, getWidth(), getHeight());
        }

        /// 文本的绘制
        //最终显示出来的文本以及颜色
        String displayText = this.textStringBuilder.toString();
        Color displayColor = this.textColor;
        if (this.textStringBuilder.isEmpty()) {
            displayText = this.tipText;
            displayColor = this.tipTextColor;
        }

        BitmapFont bitmapFont = this.getFont();
        bitmapFont.getData().setScale(FontHolder.FONT_SCALE);
        float renderHeight = TextUtil.getTextRenderHeight(bitmapFont, displayText);
        float renderX = x + this.paddingLeft;   //基于左边
        float renderY = y + renderHeight;
        //如果有背景贴图要渲染，就让字体渲染的起始高度再往上偏移一个背景的下边界高度
        if (hasBackground) renderY += background.getPadBottom();

        //裁剪字体渲染避免超出区域
        ScissorUtil.beginScissor(
            batch, GUICamera.INSTANCE.getCamera(),
            x + this.paddingLeft, y,
            getWidth() - this.paddingLeft - this.paddingRight, getHeight()
        );

        TextUtil.draw(batch, bitmapFont, displayText, renderX, renderY, displayColor);

        //恢复共享字体的缩放，防止影响其他使用同一字体的组件
        bitmapFont.getData().setScale(1f);

        /// 绘制光标（仅聚焦且可见）
        if (isFocused() && this.cursorVisible) {
            float cursorX = renderX + this.getTextWidthBeforeIndex(this.cursorIndex) + 0.5f;
            float cursorY = hasBackground ? y + background.getPadBottom() : y;
            float width   = 2f;
            float height  = renderHeight + 2f;
            this.cursorPatch.draw(batch, cursorX, cursorY, width, height);
        }

        //结束裁剪
        ScissorUtil.endScissor(batch);
    }

    /**
     * 鼠标点击：获得焦点，定位光标（交互网格左下角为原点）
     * */
    @Override
    public boolean click(GridPoint2 interactPos, int button) {
        if (!isEnabled() || !isVisible()) return false;

        //让ui屏幕设置这个组件是焦点
        getScreen().setFocusComponent(this);

        this.cursorBlinkTimer = 0;
        this.cursorVisible = true;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        float relativeX = mouseUIPosition.x - getAbsX();
        setCursorFromRelativeX(relativeX);

        this.selectionStart = -1; // 点击清除选中

        return false;
    }

    /**
     * 根据相对于组件左边界的距离设置光标索引
     * @param relativeX 距离组件左边缘的距离（0 ~ width）
     */
    private void setCursorFromRelativeX (float relativeX) {
        float clickX = relativeX - this.paddingLeft;
        if (clickX <= 0) {
            this.cursorIndex = 0;
            return;
        }

        final int length = this.textStringBuilder.length();
        BitmapFont font = this.getFont();
        font.getData().setScale(FontHolder.FONT_SCALE);
        try {
            //字符之间的组合可能会导致宽度计算有差异，这个算法才是对的，非必要勿动
            float prevPrefixWidth = 0f;
            for (int i = 0; i < length; i++) {
                String prefix = this.textStringBuilder.substring(0, i + 1);
                float prefixWidth = TextUtil.getTextRenderWidth(font, prefix);
                float charWidth = prefixWidth - prevPrefixWidth;
                if (prevPrefixWidth + (charWidth / 2f) >= clickX) {
                    this.cursorIndex = i;
                    return;
                }
                prevPrefixWidth = prefixWidth;
            }
            this.cursorIndex = length;
        }finally {
            //恢复共享字体的缩放，防止影响其他使用同一字体的组件
            font.getData().setScale(1f);
        }
    }

    /**
     * 输入字符（最终提交）—— 完美支持中文、英文等
     */
    @Override
    public boolean keyTyped(char character) {
        if (!isFocused() || !isEnabled()) return false;
        //控制字符由 keyDown 处理
        if (character == '\b' || character == '\t' || character == '\n' ||
            character == '\r' || character == 127) return false;

        if (this.textStringBuilder.length() >= this.maxLength) return false;

        //若存在选中，先删除选中文本
        if (this.selectionStart != -1 &&    this.selectionStart != this.cursorIndex) {
            deleteSelection();
        }

        this.textStringBuilder.insert(this.cursorIndex, character);
        this.cursorIndex++;
        this.selectionStart = -1;
        return false;
    }

    /**
     * 按键处理（方向键、退格、删除、Home/End、回车、ESC）
     * */
    @Override
    public boolean keyDown(int keycode) {
        if (!isFocused() || !isEnabled()) return false;

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
                getScreen().setFocusComponent(null);
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

    /**
     * 获取BitmapFont
     * */
    public BitmapFont getFont () {
        return this.getFontHolder().getFont(this.getFontSize());
    }

    /**
     * 获取文本
     * */
    public String getText () {
        return this.textStringBuilder.toString();
    }

    public UITextField setText (String newText) {
        this.textStringBuilder.setLength(0);
        this.textStringBuilder.append(newText);
        this.cursorIndex = textStringBuilder.length();
        this.selectionStart = -1;
        return this;
    }

    public StringBuilder getTextStringBuilder () {
        return this.textStringBuilder;
    }

    public UITextField setTextStringBuilder (StringBuilder newStringBuilder) {
        this.textStringBuilder = newStringBuilder;
        return this;
    }

    public UITextField setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public UITextField setTipText (String tipText) {
        this.tipText = tipText;
        return this;
    }

    // 样式设置方法（链式调用）
    public UITextField setPadding (float left, float right) {
        this.paddingLeft = left;
        this.paddingRight = right;
        return this;
    }

    public UITextField setTextColor (Color color) {
        this.textColor = color;
        return this;
    }

    public Color getTextColor () {
        return this.textColor;
    }

    public String getTipText () {
        return this.tipText;
    }

    public Color getTipTextColor () {
        return this.tipTextColor;
    }

    public UITextField setTipTextColor (Color tipTextColor) {
        this.tipTextColor = tipTextColor;
        return this;
    }

    public int getMaxLength () {
        return this.maxLength;
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

    public NinePatch getBackgroundPatch () {
        return this.backgroundPatch;
    }

    public UITextField setBackgroundPatch (NinePatch backgroundPatch) {
        this.backgroundPatch = backgroundPatch;
        return this;
    }

    public NinePatch getCursorPatch () {
        return this.cursorPatch;
    }

    public UITextField setCursorPatch (NinePatch cursorPatch) {
        this.cursorPatch = cursorPatch;
        return this;
    }
}
