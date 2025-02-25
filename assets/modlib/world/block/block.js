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

function newBlock (property, textureId) {
    return new Block(property, textureId);
}
