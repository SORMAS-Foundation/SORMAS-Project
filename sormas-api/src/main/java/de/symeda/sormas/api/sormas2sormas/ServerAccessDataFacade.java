package de.symeda.sormas.api.sormas2sormas;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface ServerAccessDataFacade {

    List<ServerAccessDataDto> getServerAccessDataList();
}
