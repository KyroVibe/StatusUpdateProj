package n30b4rt.statusupdate;

import n30b4rt.statusupdate.network.AppServer;
import n30b4rt.statusupdate.network.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class StatusUpdate extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // AppServer.getInstance().start(4242);
        if (!HttpServer.getInstance().start()) {
            System.out.println("Http server failed to start");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // AppServer.getInstance().stop();
        HttpServer.getInstance().stop();
    }
}
