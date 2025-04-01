ModLibLoader.load("Internal", "log.js");
ModLibLoader.load("Internal", "obj.js");

ModLibLoader.load("Internal", "input.js");
ModLibLoader.load("Internal", "file.js");
ModLibLoader.load("Internal", "audio.js");
ModLibLoader.load("Internal", "world/world.js");
ModLibLoader.load("Internal", "world/entity/entity.js");
ModLibLoader.load("Internal", "world/block/block.js");
ModLibLoader.load("Internal", "world/item/item.js");

/**
 * 库核心
 * <p>
 * mod要开放一个对象给其他库来调用的话就用这个
 * */
var Library = {
    _libs: new HashMap(),
    export: function (name, obj) {
        if (!this._libs.containsKey(name)) {
            this._libs.put(name, obj);
            Log.print("核心库", "库：" + name + " 导出完成！");
        }
    },
    import: function (name) {
        if (!this._libs.containsKey(name)) {
            Log.error("核心库", "名称为：" + name + " 的库不存在");
            return undefined;
        }
        return this._libs.get(name);
    }
}
