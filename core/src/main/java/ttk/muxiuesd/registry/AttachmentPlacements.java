package ttk.muxiuesd.registry;

import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.block.instance.BlockAir;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 附着物的放置规则注册
 * <p>
 * 登记每个附着物能放在哪些方块上面（"下方方块"就是它的支撑），<b>没有登记过的附着物一处都放不了</b>。
 * 新增附着物时只注册了方块、忘了来这里登记，表现就是"右键毫无反应"，不好查，别忘了。
 * <p>
 * 一个附着物只能选择一种方案（白名单或黑名单），选定的方案记录在{@link #ATTACHMENT_RULE}里，
 * 因此同一个附着物永远只会在两张表里的一张中被查到，<b>不存在"两张表都命中时谁优先"的问题</b>，
 * 用另一种方案再次注册同一个附着物只会报错并被忽略。
 * <p>
 * <b>附着物一律不能附着在空气方块上</b>（{@link Blocks#ARI}）：这是无条件的保底规则，不管登记了什么，
 * 见{@link #canPlaceOn(Block, Attachment)}，<b>也没有给子类留覆写口子</b>。所以登记了黑名单时，
 * 空列表就等于"除了空气哪都能放"。
 * <p>
 * 同一个方块既是A的白名单又是B的黑名单是合法的：规则是按附着物分开记的，两者互不影响。
 * <p>
 * 登记用的是{@link Identifier}而不是方块对象引用：每格独享实例的附着物（{@link Botany}）在世界里
 * 有无数个实例，拿对象当键会漏；{@link Identifier}是值语义，可以安全地当键。
 * <p>
 * 本类只在主线程、且{@link Blocks#init()}之后才是有效的
 * */
public final class AttachmentPlacements {
    private static final String TAG = AttachmentPlacements.class.getName();
    /**
     * 触发本类的类加载，同时登记规则数据
     * <p>
     * 由{@link MainGameScreen#show()}调用，必须在{@link Blocks#init()}之后，
     * 此刻方块都已经注册完了，下面的静态块里才能安全地引用它们
     * */
    public static void init () {
        if (!Blocks.READY) {
            Log.error(TAG, "方块还没注册完就登记了附着物的放置规则！！！"
                + "本方法必须在 Blocks.init() 之后调用，否则规则会引用到还没有赋值的方块", new IllegalStateException());
        }
        Log.print(TAG, "附着物放置规则注册完毕");
    }

    /**
     * 放置方案
     * */
    public enum Rule {
        /** 白名单：只能放在登记的那些方块上 */
        WHITELIST,
        /** 黑名单：不能放在登记的那些方块上，其他的都能放 */
        BLACKLIST
    }
    /** 白名单表：地面方块id → 能放在它上面的附着物id列表 */
    private static final LinkedHashMap<Identifier, Array<Identifier>> WHITELIST = new LinkedHashMap<>();
    /** 黑名单表：地面方块id → 不能放在它上面的附着物id列表 */
    private static final LinkedHashMap<Identifier, Array<Identifier>> BLACKLIST = new LinkedHashMap<>();
    /** 每个附着物选定的方案，一个附着物最多一条记录 —— "只能注册一种方案"就落在这里 */
    private static final HashMap<Identifier, Rule> ATTACHMENT_RULE = new HashMap<>();



    static {
        //土豆：只能种在耕地上
        registerWhitelist(Blocks.POTATO, Blocks.FARMLAND_DRY);
    }



    /**
     * 注册白名单方案：attachment 只能放在这些方块上
     * <p>
     * 同一个附着物可以分多次调用来追加方块，重复登记的方块会自动去重
     * @param grounds 允许附着的地面方块，一个都不传时该附着物一处都放不了
     * */
    public static void registerWhitelist (Attachment attachment, Block... grounds) {
        doRegister(attachment, Rule.WHITELIST, grounds);
    }

    /**
     * 注册黑名单方案：attachment 不能放在这些方块上，其他方块都能放
     * <p>
     * 同一个附着物可以分多次调用来追加方块，重复登记的方块会自动去重
     * @param grounds 禁止附着的地面方块，一个都不传时该附着物除了空气以外哪都能放
     * */
    public static void registerBlacklist (Attachment attachment, Block... grounds) {
        doRegister(attachment, Rule.BLACKLIST, grounds);
    }

    /**
     * 能不能把 attachment 放在 ground 上
     * <p>
     * 判定顺序：<br>
     * 1. 空气方块一律不行（保底规则，与登记内容无关）<br>
     * 2. 该附着物没登记过 → 不行<br>
     * 3. 白名单方案：登记过 ground 就行，否则不行<br>
     * 4. 黑名单方案：登记过 ground 就不行，否则行
     * @param ground 下方的地面方块
     * */
    public static boolean canPlaceOn (Block ground, Attachment attachment) {
        if (ground == null || attachment == null) {
            Log.error(TAG, "地面方块或附着物为null，无法查询放置规则！！！", new IllegalArgumentException());
            return false;
        }
        if (ground instanceof BlockAir) return false;

        Rule rule = ATTACHMENT_RULE.get(attachment.getIdentifier());
        //没有登记过放置规则的附着物一处都放不了
        if (rule == null) return false;

        if (rule == Rule.WHITELIST) {
            return isListed(WHITELIST, ground, attachment);
        }
        //黑名单：没被登记为"禁止"的方块就都能放
        return !isListed(BLACKLIST, ground, attachment);
    }


    /**
     * 注册的公共流程：登记方案、校验并写入对应的表
     * */
    private static void doRegister (Attachment attachment, Rule rule, Block... grounds) {
        if (attachment == null) {
            Log.error(TAG, "附着物为null，放置规则注册失败！！！", new IllegalArgumentException());
            return;
        }
        if (rule == null) {
            Log.error(TAG, "放置方案为null，附着物 " + attachment.getID() + " 的放置规则注册失败！！！",
                new IllegalArgumentException());
            return;
        }

        Identifier attachmentId = attachment.getIdentifier();
        Rule registeredRule = ATTACHMENT_RULE.get(attachmentId);
        if (registeredRule != null && registeredRule != rule) {
            Log.error(TAG, "附着物 " + attachment.getID() + " 已经注册过" + ruleName(registeredRule)
                + "方案，不能再注册" + ruleName(rule) + "方案，本次注册已忽略！！！",
                new IllegalArgumentException());
            return;
        }

        LinkedHashMap<Identifier, Array<Identifier>> table = rule == Rule.WHITELIST ? WHITELIST : BLACKLIST;
        for (Block ground : grounds) {
            if (ground == null) {
                Log.error(TAG, "地面方块为null，附着物 " + attachment.getID() + " 的本次登记已忽略！！！",
                    new IllegalArgumentException());
                continue;
            }
            //空气本来就不能附着，登记了也没有意义
            if (ground instanceof BlockAir) {
                Log.error(TAG, "空气方块不能作为附着物 " + attachment.getID() + " 的支撑，本次登记已忽略！！！",
                    new IllegalArgumentException());
                continue;
            }

            Array<Identifier> attachments = table.computeIfAbsent(ground.getIdentifier(), id -> new Array<>());
            //去重：同一个附着物同一个方块只登记一次
            if (attachments.contains(attachmentId, false)) continue;
            attachments.add(attachmentId);
        }

        //即使一个方块都没登记也要把方案记下来：空的黑名单意味着"除了空气哪都能放"
        ATTACHMENT_RULE.put(attachmentId, rule);
    }

    /**
     * 查某张表里有没有登记过（地面方块，附着物）这一对
     * */
    private static boolean isListed (LinkedHashMap<Identifier, Array<Identifier>> table,
                                     Block ground, Attachment attachment) {
        Array<Identifier> attachments = table.get(ground.getIdentifier());
        return attachments != null && attachments.contains(attachment.getIdentifier(), false);
    }

    private static String ruleName (Rule rule) {
        return rule == Rule.WHITELIST ? "白名单" : "黑名单";
    }
}
