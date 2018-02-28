package de.symeda.sormas.app.core.timeago;

import java.util.Date;

/**
 * Created by Orson on 02/01/2018.
 */

public interface IWithDate {
    String with(final long time);
    String with(final long time, final TimeAgoMessages resources);
    String with(final Date time);
    String with(final Date time, final TimeAgoMessages resources);
}
