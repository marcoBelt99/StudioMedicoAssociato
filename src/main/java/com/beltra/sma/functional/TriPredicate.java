package com.beltra.sma.functional;

/** Interfaccia funzionale da poter usare per avere più parametri nei miei predicati
 *  seguendo l'approccio della programmazione funzionale. */
@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);

    default TriPredicate<T, U, V> and(TriPredicate<T, U, V> other) {
        return (t, u, v) -> this.test(t, u, v) && other.test(t, u, v);
    }

    default TriPredicate<T, U, V> or(TriPredicate<T, U, V> other) {
        return (t, u, v) -> this.test(t, u, v) || other.test(t, u, v);
    }

    default TriPredicate<T, U, V> negate() {
        return (t, u, v) -> !this.test(t, u, v);
    }
}
