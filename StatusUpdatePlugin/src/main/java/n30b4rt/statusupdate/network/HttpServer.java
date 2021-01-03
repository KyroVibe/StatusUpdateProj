package n30b4rt.statusupdate.network;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HttpServer {

    public static final String HTML_PREFIX = "<!DOCTYPE html><html><body>";
    public static final String HTML_SUFFIX = "</body></html>";
    public static final java.time.format.DateTimeFormatter HTTP_TIME_FORMAT =
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private ServerSocket listener;
    private Thread listenerThread;
    private ArrayList<Thread> servers;

    private HttpServer() { }

    public boolean start() {
        try {
            listener = new ServerSocket(80);
            listenerThread = new Thread(this::serve);
            listenerThread.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void serve() {

        servers = new ArrayList<Thread>();

        while (true) {
            if (listener == null || listener.isClosed())
                break;

            try {
                Socket client = listener.accept();
                // System.out.println("Accepting client");
                Thread t = new Thread(() -> {
                    try {
                        Socket cli = client;
                        PrintWriter writer = new PrintWriter(cli.getOutputStream(), true);
                        // BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(cli.getOutputStream()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(cli.getInputStream()));

                        while (true) {
                            if (cli.isClosed() || !cli.isConnected())
                                break;

                            String[] req = readRequest(reader);
                            if (req.length == 0)
                                break;
                            // System.out.println("Request Read");
                            String[] header = req[0].split(" ");
                            String responseFile = HTML_PREFIX;
                            String status;
                            if (header[0].equals("GET")) {
                                if (header[1].endsWith("/")) {
                                    status = " 200 OK";
                                    responseFile += "<h2>Player Count: " + Bukkit.getOnlinePlayers().size() + "</h2><ul>";
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        responseFile += "<li>" + p.getDisplayName() + "</li>";
                                    }
                                    responseFile += "</ul>" + HTML_SUFFIX;
                                } else {
                                    status = " 404 Not Found";
                                    responseFile += "404" + HTML_SUFFIX;
                                }
                            } else {
                                status = " 501 Not Implemented";
                                responseFile += "501" + HTML_SUFFIX;
                            }
                            writeResponse(writer, header[2], status, responseFile);
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                });
                t.start();
                servers.add(t);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (listener == null)
            return;

        try {
            listenerThread.interrupt();
            servers.forEach((x) -> x.interrupt());
            servers.clear();
            listener.close();
        } catch (Exception e) {

        }
    }

    private String[] readRequest(BufferedReader reader) {
        // System.out.println("Reading request...");
        ArrayList<String> lines = new ArrayList<String>();
        while (true) {
            try {
                String line = reader.readLine();
                // System.out.println(line);
                if (line == null || line.isEmpty()) {
                    break;
                } else {
                    lines.add(line);
                }
            } catch (Exception e) {
                break;
            }
        }
        // System.out.println("r");
        String[] res = new String[lines.size()];
        return lines.toArray(res);
    }

    public void writeResponse(PrintWriter headerWriter, String protocol, String status, String file) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
        String date = now.format(HTTP_TIME_FORMAT);

        headerWriter.println(protocol + status);
        headerWriter.println("Server: StatusUpdate v1.0-Snapshot");
        headerWriter.println("Date: " + date);
        headerWriter.println("Content-Type: text/html; charset=utf-8");
        headerWriter.println("Content-Length: " + file.length());
        headerWriter.println();
        headerWriter.flush();

        headerWriter.write(file.toCharArray(), 0, file.length());
        headerWriter.flush();

        // System.out.println("Response Written");
    }

    public enum FileTypes {
        Html
    }

    private static HttpServer instance = null;
    public static HttpServer getInstance() {
        if (instance == null)
            instance = new HttpServer();
        return instance;
    }

}
