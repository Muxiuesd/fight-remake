var Music = Java.type("com.badlogic.gdx.audio.Music");
var Sound = Java.type("com.badlogic.gdx.audio.Sound");

var Audio = {
    getRegister: function (namespace) {
        var ModAudioRegister = Java.type("ttk.muxiuesd.mod.api.ModAudioRegister");
        return ModAudioRegister.getRegister(namespace);
    },
    /**
     * 获取音频播放器的实例
     * */
    getPlayer: function () {
        var AudioPlayer = Java.type("ttk.muxiuesd.audio.AudioPlayer");
        return AudioPlayer.getInstance();
    }
}
