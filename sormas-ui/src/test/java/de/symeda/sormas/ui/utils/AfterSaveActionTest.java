package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

class AfterSaveActionTest {

	private final AfterSaveAction afterSave = new AfterSaveAction();
	private final AtomicInteger popupsOpened = new AtomicInteger();

	@Test
	void runsTheActionOnceTheSaveHappens() {

		boolean saving = afterSave.commitThen(() -> afterSave.takeOnSave().run(), popupsOpened::incrementAndGet);

		assertTrue(saving);
		assertEquals(1, popupsOpened.get());
	}

	@Test
	void reportsNoSaveWhenTheCommitIsVetoed() {

		boolean saving = afterSave.commitThen(() -> {
		}, popupsOpened::incrementAndGet);

		assertFalse(saving);
		assertEquals(0, popupsOpened.get());
		assertNull(afterSave.takeOnSave(), "a vetoed commit must not leave the action for the next save");
	}

	@Test
	void waitsForASaveDeferredBehindADialog() {

		AtomicReference<Runnable> confirmDialog = new AtomicReference<>();

		boolean saving = afterSave.commitThen(() -> confirmDialog.set(afterSave.takeOnSave()), popupsOpened::incrementAndGet);

		assertTrue(saving);
		assertEquals(0, popupsOpened.get());
		confirmDialog.get().run();
		assertEquals(1, popupsOpened.get());
	}

	@Test
	void aLaterManualSaveDoesNotRunTheActionOfACancelledDialog() {

		afterSave.commitThen(afterSave::takeOnSave, popupsOpened::incrementAndGet);

		assertNull(afterSave.takeOnSave());
		assertEquals(0, popupsOpened.get());
	}
}
