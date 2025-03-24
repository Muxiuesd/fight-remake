var Block = Java.extend(Java.type("ttk.muxiuesd.world.block.Block"),{});
var Property = Java.type("ttk.muxiuesd.world.block.Block.Property");

/**
 * @param property  方块属性
 * @param textureId 方块的贴图id
 * @param texturePath 贴图的路径
 * */
function newBlock (property, textureId, texturePath) {
    return new Block(property, textureId, texturePath);
}

/**
 * @param property  方块属性
 * @param textureId 方块的贴图id，（需要贴图提前加载过）
 * */
function newBlock (property, textureId) {
    return new Block(property, textureId);
}

function newBlockSupplier (func) {
    var supplier = Java.extend(Java.type("java.util.function.Supplier"), {
        get: function (){
            return func;
        }
    });
    return new supplier();
}
