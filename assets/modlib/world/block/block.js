var Block = Java.extend(Java.type("ttk.muxiuesd.world.block.Block"),{});
var Property = Java.type("ttk.muxiuesd.world.block.Block.Property");

function newBlock (property, texturePath) {
    //var newBlock = Java.extend(Block);
    return new Block(property, texturePath);
}
