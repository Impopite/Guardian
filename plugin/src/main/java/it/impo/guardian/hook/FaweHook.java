package it.impo.guardian.hook;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import it.impo.guardian.Guardian;

public final class FaweHook {

    private final Guardian plugin;

    public FaweHook(Guardian plugin) {
        this.plugin = plugin;
    }

    public void register() {
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void onEditSession(EditSessionEvent event) {
        if (event.getStage() != EditSession.Stage.BEFORE_CHANGE || event.getWorld() == null) {
            return;
        }

        Actor actor = event.getActor();

        event.setExtent(new FaweLogExtent(
                plugin,
                event.getExtent(),
                event.getWorld(),
                (actor == null) ? null : actor.getUniqueId(),
                (actor == null) ? null : actor.getName()
        ));
    }
}
