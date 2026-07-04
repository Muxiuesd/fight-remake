package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.resource.NinePatchResource;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.world.entity.Player;

/**
 * UI面板：玩家血条
 * */
public class PlayerHealthBarUIPanel extends UIPanel {
    private PlayerSystem playerSystem;
    private NinePatchResource backgroundNinePatchResource;
    private NinePatchResource bloodNinePatchResource;


    public PlayerHealthBarUIPanel (PlayerSystem playerSystem, float x, float y) {
        super(x, y, 90, 16);
        this.playerSystem = playerSystem;

        Resource<TextureRegion> backgroundResource = Resource.ofTextureRegion(
            Fight.ID("player_health_bar_background"),
            Fight.UITexturePath("health_background.png")
        );
        Resource<TextureRegion> bloodResource = Resource.ofTextureRegion(
            Fight.ID("player_health_bar_blood"),
            Fight.UITexturePath("health_blood.png")
        );
        this.backgroundNinePatchResource = new NinePatchResource(backgroundResource, 2, 2, 2, 2);
        this.bloodNinePatchResource = new NinePatchResource(bloodResource, 2, 2, 2, 2);

        autoInteractGridSize();
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float renderX = this.getX(parent);
        float renderY = this.getY(parent);
        float width = this.getWidth();
        float height = this.getHeight();

        ///绘制玩家血条
        //绘制血槽
        this.getBackgroundNinePatch().draw(
            batch, renderX, renderY, width, height
        );
        //绘制血条
        Player player = this.playerSystem.getPlayer();
        if (player != null) {
            int length = (int) (width * (player.getCurHealth() / player.getMaxHealth()));
            //防止瞬时血量低于0的情况导致渲染有问题
            this.getBloodNinePatch().draw(batch, renderX, renderY, Math.max(length, 0), height);
        }

        super.draw(batch, parent);
    }

    public NinePatch getBackgroundNinePatch () {
        return this.backgroundNinePatchResource.getNinePatch();
    }

    public NinePatch getBloodNinePatch () {
        return this.bloodNinePatchResource.getNinePatch();
    }

    public Resource<TextureRegion> getBackgroundResource () {
        return this.backgroundNinePatchResource.getResource();
    }

    public Resource<TextureRegion> getBloodResource () {
        return this.bloodNinePatchResource.getResource();
    }
}
