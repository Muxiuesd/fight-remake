package ttk.muxiuesd.world.loottable.common;

/**
 * 战利品组的生成条件
 * <p>
 * 掉落表在把组交给 {@link LootGenerator} 抽取之前，先用条件过滤一遍，
 * 条件不满足的组整组不参与抽取（例如作物未成熟时，成熟组一条都不抽）。
 * <p>
 * 条件本身不认识任何具体目标，只接收一个"本次生成的实际状态"（成熟度），
 * 由各表的 {@code Conditions} 负责提供，这样组可以跨表复用。
 * */
public enum LootCondition {
    /** 无条件，永远参与抽取 */
    ALWAYS,
    /** 只在目标成熟时参与抽取 */
    MATURE,
    /** 只在目标未成熟时参与抽取 */
    IMMATURE;

    /**
     * 按本次生成的实际状态判断条件是否满足
     * @param mature 目标是否成熟；没有成熟度概念的目标（普通方块、墙体）一律传 false
     * */
    public boolean test (boolean mature) {
        return switch (this) {
            case ALWAYS -> true;
            case MATURE -> mature;
            case IMMATURE -> !mature;
        };
    }
}
