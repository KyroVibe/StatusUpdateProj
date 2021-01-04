package n30b4rt.statusupdate;

import n30b4rt.statusupdate.network.AppServer;
import n30b4rt.statusupdate.network.File;
import n30b4rt.statusupdate.network.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class StatusUpdate extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // AppServer.getInstance().start(4242);
        HttpServer.getInstance().registerFile(new File("/styles.css", "text/css",
            "body {\n" +
                "    color: white;\n" +
                "    text-align: center;\n" +
                "    padding-top: 125px;\n" +
                "    background-color: black;\n" +
                "    font-family: \"Lucida Console\", \"Courier New\", monospace;\n" +
                "}\n" +
                "h2 {\n" +
                "    font-size: 60px;\n" +
                "}\n" +
                "h4 {\n" +
                "    font-size: 30px;\n" +
                "}\n" +
                "ul {\n" +
                "    list-style-type: none;\n" +
                "}"
        ));
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
