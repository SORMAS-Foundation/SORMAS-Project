package de.symeda.sormas.app.core.async;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IJobDefinition {
    void preExecute();
    void execute(TaskResultHolder resultHolder);
}
