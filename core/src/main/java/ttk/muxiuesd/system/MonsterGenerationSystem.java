package ttk.muxiuesd.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.creature.EntityTarget;

/**
 * 怪物生成系统
 * */
public class MonsterGenerationSystem extends WorldSystem {
    private TimeSystem ts;
    private PlayerSystem ps;
    private EntitySystem es;
    private ChunkSystem cs;

    private float maxGenSpan = 2f;      //生成怪物时间间隔，现实秒
    private float genSpan    = 0f;

    private float minGenRange = 12f;    //小于这个范围不生怪
    private float maxGenRange = 26f;    //大于这个范围不生怪

    public MonsterGenerationSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.ts = (TimeSystem) getManager().getSystem("TimeSystem");
        this.ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        this.es = (EntitySystem) getManager().getSystem("EntitySystem");
        this.cs = (ChunkSystem) getManager().getSystem("ChunkSystem");

        /*Slime slime = new Slime();
        slime.setEntitySystem(this.es);
        double radian = Util.randomRadian();
        slime.setBounds((float) (this.ps.getPlayer().x + 16 * Math.cos(radian)),
            (float) (this.ps.getPlayer().y + 16 * Math.sin(radian)),
            1, 1);
        this.es.add(slime);*/

        for (int i = 0; i < 5; i++) {
            EntityTarget fish = new EntityTarget();
            fish.setEntitySystem(this.es);
            fish.setBounds(this.ps.getPlayer().x + 3, this.ps.getPlayer().y - 2 + i, 1, 1);
            this.es.add(fish);
        }

        /*Enemy modEnemy = (Enemy) Gets.get("testmod:zombie", Entity.class);
        modEnemy.setEntitySystem(this.es);
        this.es.add(modEnemy);*/
    }

    @Override
    public void update (float delta) {
        //非晚上不刷怪
        if (!this.ts.isNight()) {
            return;
        }

        this.genSpan += delta;
        if (this.genSpan <= this.maxGenRange) {
            //没到生成时间不刷怪
            return;
        }

        Player player = this.ps.getPlayer();
        Vector2 playerCenter = player.getCenter();

        //在生成范围内随机坐标
        for (int i = 0; i < MathUtils.random(2, 5); i++) {
            float randomRange = MathUtils.random(this.minGenRange, this.maxGenRange);
            float randomAngle = Util.randomAngle();
            float genX = (float) (playerCenter.x + randomRange * Math.cos(randomAngle));
            float genY = (float) (playerCenter.y + randomRange * Math.sin(randomAngle));
            //生成怪物
            /*Slime slime = (Slime) EntitiesReg.get("slime");
            slime.setEntitySystem(this.es);
            slime.setBounds(genX, genY, 1, 1);
            this.es.add(slime);*/

            /*Enemy modEnemy = Gets.ENEMY("testmod:zombie", this.es);
            modEnemy.setPosition(genX, genY);*/
        }
        //System.out.println("生成怪物");
        //刷怪间隔归零
        this.genSpan = 0f;
    }


    public float getMaxGenSpan () {
        return this.maxGenSpan;
    }

    public void setMaxGenSpan (float maxGenSpan) {
        this.maxGenSpan = maxGenSpan;
    }

    public float getMinGenRange () {
        return this.minGenRange;
    }

    public void setMinGenRange (float minGenRange) {
        this.minGenRange = minGenRange;
    }

    public float getMaxGenRange () {
        return this.maxGenRange;
    }

    public void setMaxGenRange (float maxGenRange) {
        this.maxGenRange = maxGenRange;
    }
}









