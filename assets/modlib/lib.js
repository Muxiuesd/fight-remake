ModLibLoader.load("Internal", "log.js");
ModLibLoader.load("Internal", "obj.js");

ModLibLoader.load("Internal", "input.js");
ModLibLoader.load("Internal", "file.js");
ModLibLoader.load("Internal", "audio.js");
ModLibLoader.load("Internal", "world/world.js");
ModLibLoader.load("Internal", "world/entity/entity.js");
ModLibLoader.load("Internal", "world/block/block.js");

var _libs = new HashMap();
var Library = {
    export: function (name, obj) {
        if (!_libs.containsKey(name)) {
            _libs.put(name, obj);
            Log.print("核心库", "库：" + name + " 导出完成！");
        }
    },
    import: function (name) {
        if (!_libs.containsKey(name)) {
            Log.error("核心库", "名称为：" + name + " 的库不存在");
            return undefined;
        }
        return _libs.get(name);
    }
}
