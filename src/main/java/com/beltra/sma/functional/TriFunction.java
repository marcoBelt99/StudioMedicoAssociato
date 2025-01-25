package com.beltra.sma.functional;

/** La uso per trasformare e farmi restituire alcune liste (trasformate). */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
