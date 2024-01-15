package de.symeda.sormas.app.event.eventparticipant;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;

public class EventParticipantSaver {

	private BaseActivity parentActivity;

	public EventParticipantSaver(BaseActivity parentActivity) {
		this.parentActivity = parentActivity;
	}

	public void saveEventParticipantLinkedToCase(EventParticipant eventParticipantToSave, boolean eventParticipantAlreadyExists) {

		new SavingAsyncTask(parentActivity.getRootView(), eventParticipantToSave) {

			@Override
			protected void onPreExecute() {
				parentActivity.showPreloader();
			}

			@Override
			protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
				DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipantToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				parentActivity.hidePreloader();

				if (!eventParticipantAlreadyExists) {
					super.onPostExecute(taskResult);
				}

				if (taskResult.getResultStatus().isSuccess()) {
					parentActivity.finish();
				}
			}
		}.executeOnThreadPool();
	}
}
