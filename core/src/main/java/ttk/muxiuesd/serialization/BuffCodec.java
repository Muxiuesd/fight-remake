package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * 活物实体状态效果的编解码器
 * */
public class BuffCodec extends JsonCodec<LinkedHashMap<StatusEffect, StatusEffect.Data>> {
    @Override
    public void encode (LinkedHashMap<StatusEffect, StatusEffect.Data> effects, JsonDataWriter dataWriter) {
        for (StatusEffect effect : effects.keySet()) {
            StatusEffect.Data data = effects.get(effect);
            dataWriter
                .objStart(effect.getId())
                .writeFloat("duration", data.getDuration())
                .writeInt("level", data.getLevel())
                .objEnd();
        }
    }

    @Override
    public Optional<LinkedHashMap<StatusEffect, StatusEffect.Data>> parse (JsonDataReader dataReader) {
        LinkedHashMap<StatusEffect, StatusEffect.Data> effects = new LinkedHashMap<>();

        dataReader.getParse().forEach(effectJsonValue -> {
            if (effectJsonValue.isObject()) {
                String id = effectJsonValue.name();
                if (Registries.STATUS_EFFECT.contains(id)) {
                    //获取注册的状态效果
                    StatusEffect effect = Registries.STATUS_EFFECT.get(id);
                    //读取状态效果的等级和持续时间
                    float duration = effectJsonValue.getFloat("duration", 114.5f);
                    int level = effectJsonValue.getInt("level", 114);
                    StatusEffect.Data data = new StatusEffect.Data(duration, level);
                    effects.put(effect, data);
                }else {
                    Log.error(BuffCodec.class.getName(), "有不存在的状态效果ID：" + id + " ，跳过读取！！！");
                }
            }
        });

        return Optional.of(effects);
    }
}
