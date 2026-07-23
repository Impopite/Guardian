package it.impo.protect.database;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.database.ProtectTable;
import it.impo.protect.api.database.impl.BlockLogTable;
import it.impo.protect.api.database.impl.ContainerLogTable;
import it.impo.protect.api.database.impl.InteractLogTable;
import it.impo.protect.api.database.impl.ItemLogTable;
import it.impo.protect.database.impl.BaseBlockLogTable;
import it.impo.protect.database.impl.BaseContainerLogTable;
import it.impo.protect.database.impl.BaseInteractLogTable;
import it.impo.protect.database.impl.BaseItemLogTable;

import java.sql.SQLException;

public class BaseProtectTable extends ProtectTable {

    private final BlockLogTable blockLogTable;
    private final ContainerLogTable containerLogTable;
    private final InteractLogTable interactLogTable;
    private final ItemLogTable itemLogTable;

    public BaseProtectTable(HikariDataSource dataSource) {
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
