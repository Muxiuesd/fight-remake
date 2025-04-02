
var Blocks = {
    properties: {
        newProperty: function () {
            var p = Java.type("ttk.muxiuesd.world.block.abs.Block.Property");
            return new p;
        }
    },
    getAbstractBlock: function (obj) {
        return Java.extend(Java.type("ttk.muxiuesd.world.block.abs.Block"), obj);
    },
    /**
     * @param property  方块属性
     * @param textureId 方块的贴图id，（需要贴图提前加载过）
     * */
    newBlock: function (property, textureId) {
        var Block = this.getAbstractBlock({});
        return new Block(property, textureId);
    },
    /**
     * @param property  方块属性
     * @param textureId 方块的贴图id
     * @param texturePath 贴图的路径
     * */
    newBlock: function (property, textureId, texturePath) {
        var Block = this.getAbstractBlock({});
        return new Block(property, textureId, texturePath);
    },
    newSupplier: function (func) {
        var supplier = Java.extend(Java.type("ttk.muxiuesd.mod.api.ModSupplier"), {
            getNew: function (){
                return func;
            }
        });
        return new supplier();
    }
}
