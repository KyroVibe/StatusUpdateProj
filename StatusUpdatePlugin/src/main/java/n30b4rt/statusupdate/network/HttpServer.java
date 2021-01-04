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
import java.util.HashMap;

public class HttpServer {

    public static final String HTML_PREFIX = "<!DOCTYPE html><html>";
    public static final String HTML_SUFFIX = "</html>";
    public static final java.time.format.DateTimeFormatter HTTP_TIME_FORMAT =
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private HashMap<String, File> files;

    private ServerSocket listener;
    private Thread listenerThread;
    private ArrayList<Thread> servers;

    private HttpServer() {
        files = new HashMap<String, File>();
        // Register Defaults
        files.put("404", new File("404", "text/html",
            HTML_PREFIX + "File not found" + HTML_SUFFIX));
        files.put("501", new File("501", "text/html",
            HTML_PREFIX + "That too complex for me bruv" + HTML_SUFFIX));
    }

    public boolean registerFile(File file) {
        if (files.containsKey(file.getFileName()))
            return false;
        files.put(file.getFileName(), file);
        return true;
    }

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
                            // String responseFile = HTML_PREFIX;
                            File responseFile = files.get("501");
                            String status = " 501 Not Implemented";
                            if (header[0].equals("GET")) {
                                if (header[1].equals("/")) {
                                    System.out.println("Req: " + header[1]);
                                    status = " 200 OK";
                                    responseFile = new File("/", "text/html", genIndex());
                                } else if (files.containsKey(header[1])) {
                                    status = " 200 OK";
                                    responseFile = files.get(header[1]);
                                } else {
                                    status = " 404 Not Found";
                                    // responseFile += "404" + HTML_SUFFIX;
                                    responseFile = files.get("404");
                                    System.out.println("404: \"" + header[1] + "\"");
                                }
                                // System.out.println("Method: " + header[0]);
                            } else {
                                System.out.println("501: " + header[0]);
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

    private String genIndex() {
        String html =
            HTML_PREFIX + "<head><link rel=\"stylesheet\" href=\"styles.css\"></head>" +
                "<body><h2>Player Count: " + Bukkit.getOnlinePlayers().size() + "</h2>";
        for (Player p : Bukkit.getOnlinePlayers()) {
            html += "<h4>" + p.getDisplayName() + "</h4>";
        }
        html += "</body>" + HTML_SUFFIX;
        return html;
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

    public void writeResponse(PrintWriter headerWriter, String protocol, String status, File file) {
        headerWriter.println(protocol + status);
        headerWriter.println("Server: StatusUpdate v1.0-Snapshot");
        headerWriter.println("Date: " + getDate());
        headerWriter.println("Content-Type: " + file.getType() + "; charset=utf-8");
        headerWriter.println("Content-Length: " + file.getSize());
        headerWriter.println();
        headerWriter.flush();

        headerWriter.write(file.getData(), 0, file.getSize());
        headerWriter.flush();

        // System.out.println("Response Written");
    }

    public static String getDate() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
        return now.format(HTTP_TIME_FORMAT);
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
