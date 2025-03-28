package ttk.muxiuesd.mod;

import java.util.HashSet;
import java.util.Set;

/**
 * mod节点
 * */
public class ModNode {
    public final Mod mod;
    private Set<ModNode> parents;
    private Set<ModNode> children;

    public ModNode (Mod mod) {
        this.mod = mod;
        this.parents = new HashSet<ModNode>();
        this.children = new HashSet<ModNode>();
    }

    /**
     * 运行mod
     * */
    public boolean run () {
        //没有依赖的，也没有被依赖，就直接执行完事
        if (children.isEmpty() && parents.isEmpty() && !mod.isRunning()) {
            this.mod.run();
            return this.mod.isRunning();
        }

        //有依赖就先加载依赖
        if (!children.isEmpty()) {
            for (ModNode child : children) {
                if (child.mod.isRunning()) {
                    continue;
                }
                //跳到子节点的run
                boolean successful = child.run();
                if (!successful) {
                    throw new RuntimeException("Mod：" + mod.getModName() + " 的依赖："
                        + child.mod.getModName() + " 运行失败！！！");
                }
            }
            //加载完所有依赖后再执行自己的run
            this.mod.run();
            return this.mod.isRunning();
        }
        //到这里说明没有依赖，但是被其他mod依赖，则也是执行自己
        this.mod.run();
        return this.mod.isRunning();
    }

    public Set<ModNode> getParents () {
        return this.parents;
    }

    public void addParent (ModNode parent) {
        this.parents.add(parent);
    }

    public Set<ModNode> getChildren () {
        return this.children;
    }

    public void addChild (ModNode child) {
        this.children.add(child);
        child.addParent(this);
    }
}
