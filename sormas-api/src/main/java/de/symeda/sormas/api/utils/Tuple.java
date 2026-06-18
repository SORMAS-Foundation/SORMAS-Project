package de.symeda.sormas.api.utils;

import java.util.Objects;

public class Tuple<F, S> {

	private final F first;
	private final S second;

	public static <F, S> Tuple<F, S> of(final F first, final S second) {
		return new Tuple<>(first, second);
	}

	public static <F, S> Tuple<F, S> firstOnly(final F first) {
		return new Tuple<>(first, null);
	}

	public static <F, S> Tuple<F, S> secondOnly(final S second) {
		return new Tuple<>(null, second);
	}

	public Tuple(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Tuple{" + "first=" + first + ", second=" + second + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Tuple<?, ?> tuple = (Tuple<?, ?>) o;
		return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}
