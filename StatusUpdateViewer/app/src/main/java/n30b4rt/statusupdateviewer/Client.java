package n30b4rt.statusupdateviewer;

import android.util.Pair;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import n30b4rt.Status;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private Client() { }

    public void start(String address, IFuncCallbacks callback) {
        Thread t = new Thread(() -> {
            String a = address;
            try {
                socket = new Socket(address, 4242);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                callback.PossibleException(null);
            } catch (Exception e) { callback.PossibleException(e); }
        });
        t.start();
    }

    public Pair<Status, Exception> getStatus() {
        Gson gson = new Gson();
        try {
            writer.write("status\n");
            writer.flush();
            String json = reader.readLine();
            if (json == null || json.isEmpty())
                return new Pair<Status, Exception>(null, new Exception("json text is empty"));
            Status s = gson.fromJson(json, Status.class);
            return new Pair<Status, Exception>(s, null);
        } catch (Exception e) {
            return new Pair<Status, Exception>(null, e);
        }
    }

    public void stop(IFuncCallbacks callbacks) {
        Thread t = new Thread(() -> {
            try {
                reader.close();
                writer.close();
                socket.close();
                callbacks.PossibleException(null);
            } catch (Exception e) {
                callbacks.PossibleException(e);
            }
        });
        t.start();
    }

    private static Client instance = null;
    public static Client getInstance() {
        if (instance == null)
            instance = new Client();
        return instance;
    }
}
