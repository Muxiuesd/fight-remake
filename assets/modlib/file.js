var Texture = Java.type("com.badlogic.gdx.graphics.Texture");
var TextureRegion = Java.type("com.badlogic.gdx.graphics.g2d.TextureRegion");

var ModFileLoader = Java.type("ttk.muxiuesd.mod.api.ModFileLoader");

var File = {
    /**
     * 获取文件加载器
     * */
    getFileLoader: function (namespace) {
        return ModFileLoader.getFileLoader(namespace);
    },
    /**
     * 加载指定文件的快捷函数
     * @param namespace 命名空间
     * @param id id
     * @param path 相对于mod文件夹的路径
     * @param type 要加载的文件类型
     * */
    loadFile: function (namespace, id, path, type) {
        this.loadFile(namespace, id, path, type, undefined);
    },
    /**
     * 加载指定文件的快捷函数
     * @param namespace 命名空间
     * @param id id
     * @param path 相对于mod文件夹的路径
     * @param type 要加载的文件类型
     * @param callback 回调
     * */
    loadFile: function (namespace, id, path, type, callback) {
        var loader = this.getFileLoader(namespace);
        loader.load(id, path, type, new FileLoadCallback(callback));
    }
}


/**
 * 文件加载回调
 * */
function FileLoadCallback (callback) {
    /**
     * @param file 为加载完成后传回的文件
     */
    this.run = function (file) {
        if (callback !== undefined) {
            callback(file);
        }
    }
}
