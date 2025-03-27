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

    public boolean run () {
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
