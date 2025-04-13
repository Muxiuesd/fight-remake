package ttk.muxiuesd.registrant;

import java.util.function.Function;

public interface GameSupplier<T, R> extends Function<T, R> {
    @Override
    R apply (T t);
}
