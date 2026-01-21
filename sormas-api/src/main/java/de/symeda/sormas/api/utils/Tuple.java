package de.symeda.sormas.api.utils;

public class Tuple<F, S> {

	private final F first;
	private final S second;

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
}
