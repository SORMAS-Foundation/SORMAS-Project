package de.symeda.sormas.backend.patch;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

/**
 * Simple type to check if a Lazy Loaded supplier was already loaded.
 * 
 * @param <T>
 *            supplier type.
 */
public class LazySupplier<T> implements Supplier<T> {

	private final com.google.common.base.Supplier<T> supplier;
	private boolean loaded = false;

	private LazySupplier(com.google.common.base.Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static <T> LazySupplier<T> of(Supplier<T> supplier) {
		return new LazySupplier<>(Suppliers.memoize(supplier::get));
	}

	@Override
	public T get() {
		loaded = true;
		return supplier.get();
	}

	public boolean isLoaded() {
		return loaded;
	}
}
