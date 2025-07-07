package ttk.muxiuesd.interfaces.render;

/**
 * 渲染接口识别器
 * */
public interface IRenderTaskRecognizer {
    /**
     * 识别成功返回true，就不再继续往下识别
     * */
    boolean recognize(IRenderTask task);
}
