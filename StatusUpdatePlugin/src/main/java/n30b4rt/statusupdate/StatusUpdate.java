package n30b4rt.statusupdate;

import org.bukkit.plugin.java.JavaPlugin;

public final class StatusUpdate extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Server.getInstance().start(4242);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Server.getInstance().stop();
    }
}
