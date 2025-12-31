package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.EffectUI;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.LinkedHashMap;


/**
 * 玩家状态效果的UI面板
 * */
public class PlayerEffectUIPanel extends UIPanel {

    private PlayerSystem playerSystem;
    private LinkedHashMap<StatusEffect, EffectUI> effectUIs;


    public PlayerEffectUIPanel (float x, float y,
                                float width, float height,
                                GridPoint2 interactGridSize,
                                PlayerSystem playerSystem) {
        super(x, y, width, height, interactGridSize);

        this.playerSystem = playerSystem;
        this.effectUIs = new LinkedHashMap<>();
    }

    @Override
    public void update (float delta) {
        super.update(delta);

        //计算需要展示的状态UI组件
        Player player = this.playerSystem.getPlayer();
        LinkedHashMap<StatusEffect, StatusEffect.Data> playerEffects = player.getEffects();

        int index = 1;

        for (StatusEffect effect : playerEffects.keySet()) {
            //如果存在这个状态的UI，就更新对应的UI组件的值
            if (this.effectUIs.containsKey(effect)) {
                EffectUI effectUI = this.effectUIs.get(effect);
                effectUI
                    .setPosition(getX(), getY() - index * effectUI.getHeight())
                    .update(delta);
            }else {
                //如果不存在这个状态的UI，就新建一个并且加入
                EffectUI effectUI = new EffectUI()
                    .setStatusEffect(effect)
                    .setEffectData(playerEffects.get(effect));

                effectUI.setPosition(getX(), getY() - index * effectUI.getHeight());

                this.effectUIs.put(effect, effectUI);
            }

            index++;
        }

        //检查玩家的状态是否与这里面的状态UI有对应的，没有的话就删除
        for (StatusEffect effect: this.effectUIs.keySet()) {
            if (!playerEffects.containsKey(effect)) {
                this.effectUIs.remove(effect);
            }
        }

        getComponents().clear();
        this.effectUIs.values().forEach(this::addComponent);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        super.draw(batch, parent);
    }

    /**
     * 绘制一个状态效果UI
     * */
    private void drawEffectUI (Batch batch) {

    }
}
