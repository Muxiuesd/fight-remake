package ttk.muxiuesd.interfaces;

/**
 * 区块遍历所执行的任务的接口
 * */
@FunctionalInterface
public interface ChunkTraversalJob{
   void execute (int x, int y);
}
