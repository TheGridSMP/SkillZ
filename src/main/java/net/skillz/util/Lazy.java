package net.skillz.util;

import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> supplier;
    public T value;

    private boolean cached;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (this.cached)
            return value;

        this.cached = true;
        this.value = supplier.get();
        return value;
    }

    public void invalidate() {
        this.cached = false;
        this.value = null;
    }
}
