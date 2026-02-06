package game.muxiuesd.bedrockcore.app.interfaces.data;

public interface IDataOutput<T extends DataWriter<?>> {
    void output (T writer);
}
