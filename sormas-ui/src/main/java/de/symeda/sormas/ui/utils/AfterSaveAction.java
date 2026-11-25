package de.symeda.sormas.ui.utils;

public class AfterSaveAction {

	private Runnable pending;
	private boolean commitReachedSave;

	public boolean commitThen(Runnable commit, Runnable action) {

		commitReachedSave = false;
		pending = action;
		commit.run();
		if (!commitReachedSave) {
			pending = null;
		}
		return commitReachedSave;
	}

	public Runnable takeOnSave() {

		commitReachedSave = true;
		Runnable action = pending;
		pending = null;
		return action;
	}
}
