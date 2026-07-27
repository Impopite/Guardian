package it.impo.guardian.api;

import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.api.manager.GuardianManager;
import it.impo.guardian.api.manager.RollbackManager;

public interface GuardianApi{

    GuardianTable getGuardianTable();

    GuardianManager getGuardianManager();

    RollbackManager getRollbackManager();

}
