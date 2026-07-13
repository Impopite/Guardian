package it.impo.protect.api;

import it.impo.protect.api.database.ProtectTable;
import it.impo.protect.api.manager.ProtectManager;

public interface ProtectApi{

    ProtectTable getProtectTable();

    ProtectManager getProtectManager();

}
