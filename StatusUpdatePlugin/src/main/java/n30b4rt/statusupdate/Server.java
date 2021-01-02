package n30b4rt.statusupdate;

import com.google.gson.Gson;
import n30b4rt.Status;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {

    private ServerSocket serverSocket;
    private Thread listener;
    private ArrayList<Thread> servers;

    private Server() { }

    public boolean start(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
            listener = new Thread(this::serve);
            listener.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void serve() {

        servers = new ArrayList<Thread>();

        while (true) {
            if (serverSocket == null || serverSocket.isClosed())
                break;

            try {
                Socket client = serverSocket.accept();
                Thread serverThread = new Thread(() -> {
                    // I think by creating a reference to the socket that stays in the scope, the object will
                    // avoid being GC... I think...
                    try {
                        Socket cli = client;
                        PrintWriter writer = new PrintWriter(cli.getOutputStream(), true);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                        String msg;
                        while (true) {
                            if (cli.isClosed() || !cli.isConnected())
                                break;

                            msg = reader.readLine();
                            // System.out.println("Received message: \"" + msg + "\"");
                            switch (msg.toLowerCase()) {
                                case "status":
                                    Status s = new Status();
                                    s.PlayerCount = Bukkit.getOnlinePlayers().size();
                                    String json = new Gson().toJson(s, Status.class);
                                    // System.out.println(json);
                                    writer.write(json + "\n");
                                    writer.flush();
                                    break;
                            }
                        }
                        writer.close();
                        reader.close();
                        cli.close();
                    } catch (Exception e) {
                        // System.out.println("Exception in client server. Abandoning client.");
                    }
                });
                serverThread.start();
                servers.add(serverThread);
            } catch (Exception e) {
                // System.out.println("Exception while starting client server. Abandoning client.");
            }
        }
    }

    public boolean stop() {
        if (serverSocket == null)
            return true;

        try {
            listener.interrupt();
            servers.forEach((x) -> x.interrupt());
            servers.clear();
            serverSocket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Server instance = null;
    public static Server getInstance() {
        if (instance == null)
            instance = new Server();
        return instance;
    }
}
