package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class TaskExecutorFor {
    public static ITaskExecutor job(IJobDefinition jobDefinition) {
        return new TaskExecutor(jobDefinition);

    }
}
