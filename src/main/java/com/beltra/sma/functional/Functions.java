package com.beltra.sma.functional;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Functions {

    public static <A, B, R> List<R> zip(
            List<A> listA,
            List<B> listB,
            BiFunction<A, B, R> zipper
    ) {
        int size = Math.min(listA.size(), listB.size());
        return IntStream.range(0, size)
                .mapToObj(i -> zipper.apply(listA.get(i), listB.get(i)))
                .collect(Collectors.toList());
    }
}
