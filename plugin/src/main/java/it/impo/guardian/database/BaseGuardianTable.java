package it.impo.guardian.database;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.api.database.impl.BlockLogTable;
import it.impo.guardian.api.database.impl.ContainerLogTable;
import it.impo.guardian.api.database.impl.InteractLogTable;
import it.impo.guardian.api.database.impl.ItemLogTable;
import it.impo.guardian.database.impl.BaseBlockLogTable;
import it.impo.guardian.database.impl.BaseContainerLogTable;
import it.impo.guardian.database.impl.BaseInteractLogTable;
import it.impo.guardian.database.impl.BaseItemLogTable;

import java.sql.SQLException;

public class BaseGuardianTable extends GuardianTable {

    private final BlockLogTable blockLogTable;
    private final ContainerLogTable containerLogTable;
    private final InteractLogTable interactLogTable;
    private final ItemLogTable itemLogTable;

    public BaseGuardianTable(HikariDataSource dataSource) {
        this.blockLogTable = new BaseBlockLogTable(dataSource);
        this.containerLogTable = new BaseContainerLogTable(dataSource);
        this.interactLogTable = new BaseInteractLogTable(dataSource);
        this.itemLogTable = new BaseItemLogTable(dataSource);
    }

    @Override
    public void createTables() throws SQLException {
        this.blockLogTable.createTable();
        this.containerLogTable.createTable();
        this.interactLogTable.createTable();
        this.itemLogTable.createTable();
    }

    @Override
    public void removeOldLogsAll(int days) {
        this.blockLogTable.removeOldLog(days);
        this.containerLogTable.removeOldLog(days);
        this.interactLogTable.removeOldLog(days);
        this.itemLogTable.removeOldLog(days);
    }

    @Override
    public BlockLogTable getBlockLogTable() {
        return blockLogTable;
    }

    @Override
    public ContainerLogTable getContainerLogTable() {
        return containerLogTable;
    }

    @Override
    public InteractLogTable getInteractLogTable() {
        return interactLogTable;
    }

    @Override
    public ItemLogTable getItemLogTable() {
        return itemLogTable;
    }
}
