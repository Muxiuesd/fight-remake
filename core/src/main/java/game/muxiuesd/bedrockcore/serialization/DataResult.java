package game.muxiuesd.bedrockcore.serialization;

import java.util.Optional;
import java.util.function.Function;

/**
 * 编解码器的结果容器，含有错误处理
 * */
public abstract class DataResult<T> {
    public static <T> DataResult<T> success (T value) { return new Success<>(value); }
    public static <T> DataResult<T> error (String message) { return new Error<>(message); }
    public static <T> DataResult<T> error (String message, T partial) { return new ErrorWithPartial<>(message, partial); }

    public abstract Optional<T> result();

    public abstract Optional<String> error();

    public abstract boolean isSuccess();

    public <R> DataResult<R> map(Function<T, R> mapper) {
        if (this instanceof Success) return success(mapper.apply(((Success<T>)this).value));
        if (this instanceof ErrorWithPartial) {
            ErrorWithPartial<T> e = (ErrorWithPartial<T>)this;
            return error(e.message, mapper.apply(e.partial));
        }
        return error(((Error<T>)this).message);
    }

    public <R> DataResult<R> flatMap(Function<T, DataResult<R>> fn) {
        if (this instanceof Success) return fn.apply(((Success<T>)this).value);
        if (this instanceof ErrorWithPartial) {
            ErrorWithPartial<T> e = (ErrorWithPartial<T>)this;
            DataResult<R> next = fn.apply(e.partial);
            if (next instanceof Success) return error(e.message, ((Success<R>)next).value);
            if (next instanceof Error) return error(e.message + "; " + ((Error<?>)next).message);
            ErrorWithPartial<R> n = (ErrorWithPartial<R>)next;
            return error(e.message + "; " + n.message, n.partial);
        }
        return error(((Error<T>)this).message);
    }

    public static class Success<T> extends DataResult<T> {
        public final T value;
        Success(T value) { this.value = value; }
        public Optional<T> result() { return Optional.of(value); }
        public Optional<String> error() { return Optional.empty(); }
        public boolean isSuccess() { return true; }
    }

    public static class Error<T> extends DataResult<T> {
        final String message;
        Error(String message) { this.message = message; }
        public Optional<T> result() { return Optional.empty(); }
        public Optional<String> error() { return Optional.of(message); }
        public boolean isSuccess() { return false; }
    }

    public static class ErrorWithPartial<T> extends Error<T> {
        final T partial;
        ErrorWithPartial(String message, T partial) { super(message); this.partial = partial; }
        @Override public Optional<T> result() { return Optional.of(partial); }
    }
}
