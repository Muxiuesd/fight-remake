package ttk.muxiuesd.ui.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.components.InfoEntry;
import ttk.muxiuesd.ui.components.infoentries.InfoEntryFPS;
import ttk.muxiuesd.util.TextUtil;
import ttk.muxiuesd.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息面板（仿 Minecraft F3 调试信息面板）
 * <p>
 * 显示若干条信息条目，至上而下排列。每一条目占一行，行背景为半透明黑色框，文字用游戏字体渲染。
 * 按 I 键开关本面板。
 */
public class InfoPanel extends UIPanel {
    /// 行相关尺寸
    public static final float ROW_SPACING = 1f;          //行与行之间的间距
    public static final float BOX_PADDING_X = 4f;        //半透明黑框的左右内边距
    public static final float BOX_PADDING_Y = 1f;        //半透明黑框的上下内边距
    public static final int FONT_SIZE = 16;               //信息字体大小
    public static final float FONT_SCALE = 0.5f;
    public static final float BOX_ALPHA = 0.3f;   //半透明黑框的不透明度

    /// 单例模式
    private static InfoPanel INSTANCE;

    public static InfoPanel getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new InfoPanel();
        }
        return INSTANCE;
    }


    private final List<InfoEntry> infoEntries = new ArrayList<>();

    /// 面板锚定（默认停靠屏幕左上角）
    private float marginX = 2f;      //距屏幕左边距
    private float marginY = 4f;      //距屏幕顶边距

    private InfoPanel () {
        super(0, 0, 0, 0, new GridPoint2(1, 1));
        //默认附带一条 FPS 信息（每秒刷新，显示上一秒平均帧率）
        //this.addEntry(InfoEntry.of("FPS", () -> String.valueOf(this.getLastSecondAvgFps())));

        this.addEntry(new InfoEntryFPS());

        //信息面板只展示、不参与交互，禁用交互避免干扰鼠标点击与命中检测
        this.setEnabled(false);
    }

    /**
     * 添加一个信息条目
     */
    public InfoPanel addEntry (InfoEntry entry) {
        this.infoEntries.add(entry);
        return this;
    }

    @Override
    public void update (float delta) {
        //按 I 键开关面板
        if (KeyBindings.InfoPanelToggle.wasJustPressed()) {
            this.setVisible(!this.isVisible());
        }
        if (!this.isVisible()) return;

        this.infoEntries.forEach(entry -> entry.update(delta));

        super.update(delta);
    }

    /**
     * 屏幕视口大小变化时，让面板始终停靠屏幕左上角
     * <p>
     * GUI 坐标系原点在屏幕中心、y 轴向上，因此左上角 = (-width/2 + 边距, height/2 - 边距)
     */
    @Override
    public void resize (float viewportWidth, float viewportHeight) {
        super.resize(viewportWidth, viewportHeight);
        this.setPosition(- viewportWidth / 2f + this.marginX, viewportHeight / 2f - this.marginY);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (!this.isVisible()) return;
        if (this.infoEntries.isEmpty()) return;

        BitmapFont font = Fonts.MC.getFont(FONT_SIZE);
        font.getData().setScale(FONT_SCALE);
        //字体的真实度量：getAscent() 为正（基线到字形顶），getDescent() 为负（基线到字形底，方向向下）。
        //不能直接用 ascent + descent 算行高（会因 descent 为负而严重缩小），必须用 getLineHeight()（保证为正）。
        float ascent = font.getAscent();
        float lineHeight = font.getLineHeight();
        //行高 = 字形总高 + 上下内边距，保证黑框能完整包裹字形
        float rowHeight = lineHeight + BOX_PADDING_Y * 2f;

        float x = this.getX();
        float y = this.getY();

        //渲染所有的信息条目
        for (int i = 0; i < this.infoEntries.size(); i++) {
            String text = this.infoEntries.get(i).getTextString();
            //测量文本实际渲染宽度，据此决定黑框宽度
            float textWidth = TextUtil.getTextRenderWidth(font, text);
            //行的顶部：GUI 坐标系 y 轴向上，面板锚点在顶部，行从上往下排
            float rowTop = y - i * (rowHeight + ROW_SPACING);
            //黑框底部：SpriteBatch.draw 的 y 是矩形的底部，矩形向上延伸 rowHeight，
            //故黑框覆盖 [rowTop-rowHeight, rowTop]，恰好是一行，且首行不超出屏幕顶部
            float boxBottom = rowTop - rowHeight + 1f;
            //文字基线：让字形顶部正好落在黑框顶部内侧（顶部留 padding），
            //即 baseline + ascent = rowTop - padding → baseline = rowTop - padding - ascent；
            //此时字形底 = baseline + descent = boxBottom + padding，底部也留 padding，字形精确居中于黑框
            float textBaseline = rowTop - BOX_PADDING_Y - ascent;

            //半透明黑色背景框（方案A：1×1 白像素 + batch.setColor 调整颜色与透明度）
            batch.setColor(0f, 0f, 0f, BOX_ALPHA);
            batch.draw(Util.getWhitePixel(),
                x, boxBottom,
                textWidth + BOX_PADDING_X * 2,
                rowHeight);
            batch.setColor(Color.WHITE);

            //绘制文本（TextUtil.draw 的 y 是基线，字形从基线向上延伸）
            TextUtil.draw(batch, font, text, x + BOX_PADDING_X, textBaseline);
        }
        //恢复大小
        font.getData().setScale(1f);
    }
}
