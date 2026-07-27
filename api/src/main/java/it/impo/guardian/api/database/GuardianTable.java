package it.impo.guardian.api.database;

import it.impo.guardian.api.database.impl.BlockLogTable;
import it.impo.guardian.api.database.impl.ContainerLogTable;
import it.impo.guardian.api.database.impl.InteractLogTable;
import it.impo.guardian.api.database.impl.ItemLogTable;

import java.sql.SQLException;

public abstract class GuardianTable {

    public abstract BlockLogTable getBlockLogTable();

    public abstract ContainerLogTable getContainerLogTable();

    public abstract InteractLogTable getInteractLogTable();

    public abstract ItemLogTable getItemLogTable();

    public abstract void createTables() throws SQLException;

    public abstract void removeOldLogsAll(int days);
}
