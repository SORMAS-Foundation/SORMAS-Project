package de.symeda.sormas.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RequestContextHolderTest {

	@AfterEach
	void resetContext() {
		RequestContextHolder.reset();
	}

	@Test
	void flagsTheThreadOnlyWhileTheImportRuns() {
		AtomicBoolean seenInside = new AtomicBoolean();

		RequestContextHolder.runInImportContext(() -> seenInside.set(RequestContextHolder.isImport()));

		assertTrue(seenInside.get());
		assertFalse(RequestContextHolder.isImport());
	}

	@Test
	void restoresTheContextWhenTheImportFails() {
		assertThrows(IllegalStateException.class, () -> RequestContextHolder.runInImportContext(() -> {
			throw new IllegalStateException("failed");
		}));

		assertFalse(RequestContextHolder.isImport());
	}

	@Test
	void keepsTheMobileSyncFlag() {
		RequestContextHolder.setRequestContext(new RequestContextTO(true));
		AtomicBoolean mobileSyncInside = new AtomicBoolean();

		RequestContextHolder.runInImportContext(() -> mobileSyncInside.set(RequestContextHolder.isMobileSync()));

		assertTrue(mobileSyncInside.get());
		assertTrue(RequestContextHolder.isMobileSync());
		assertFalse(RequestContextHolder.isImport());
	}
}
