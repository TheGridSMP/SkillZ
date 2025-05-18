package net.skillz.util;

@FunctionalInterface
public interface IntObjectBiConsumer<T> {
    void accept(int i, T t);
}