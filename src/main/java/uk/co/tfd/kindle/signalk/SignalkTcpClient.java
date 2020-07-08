package uk.co.tfd.kindle.signalk;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 19/06/2020.
 */
public class SignalkTcpClient extends StatusUpdates  {
    private static final Logger log = LoggerFactory.getLogger(SignalkTcpClient.class);
    private final SignalkDiscovery discovery;
    private List<SignalKServer> servers;
    private final SignalkHttpClient httpClient;
    private int serverNo = -1;
    private final Data.Store store;
    private boolean running;
    private long nextFetch = 0;
    private Thread thread;
    private Object waitForServers = new Object();

    public SignalkTcpClient(Data.Store store, SignalkHttpClient httpClient, Map<String, Object> config) {
        this.httpClient = httpClient;
        this.store = store;
        this.discovery = new SignalkDiscovery(this);
        this.servers = new ArrayList<>();
        List<Map<String, Object>> configServers = (List<Map<String, Object>>) config.get("servers");
        if (configServers != null) {
            for(Map<String, Object> configServer : configServers) {
                servers.add(new SignalKServer(configServer));
            }
        }

    }

    public void removeServer(String id) {
        synchronized (waitForServers) {
            for(int i = 0; i < servers.size(); i++) {
                SignalKServer server = servers.get(i);
                if ( server.isId(id)) {
                    log.info("Remove {} ",id);
                    servers.remove(i);
                    return;
                }
            }
        }
    }

    public void addHttp(String id, String url) {
        synchronized (waitForServers) {
            for (SignalKServer server : servers) {
                if (id.equals(server.getId())) {
                    server.setUrl(url);
                    log.info("Update http {}  {} ", id, url);
                    if (server.isComplete()) {
                        log.info("Notify ");
                        waitForServers.notifyAll();
                    }
                    return;
                }
            }
            log.info("Adding http {}  {} ", id, url);
            SignalKServer server = new SignalKServer(id);
            server.setUrl(url);
            servers.add(server);
        }

    }

    public void addTcp(String id, String host, long port) {
        synchronized (waitForServers) {
            for (SignalKServer server : servers) {
                if (server.isId(id)) {
                    server.setHost(host);
                    server.setPort(port);
                    log.info("Update tcp {}  {} ", id, port);
                    if (server.isComplete()) {
                        log.info("Notify ");
                        waitForServers.notifyAll();
                    }
                    return;
                }
            }
            log.info("Adding tcp {}  {} ", id, port);
            SignalKServer server = new SignalKServer(id);
            server.setHost(host);
            server.setPort(port);
            servers.add(server);
        }

    }


    private SignalKServer getServer() {
        synchronized (waitForServers) {

            for (int i = 0; i < servers.size(); i++) {
                serverNo = (serverNo + 1) % servers.size();
                SignalKServer server = servers.get(serverNo);
                if (server.isAvailable()) {
                    return server;
                }
            }
            log.info("Waiting for servers to be discovered");
            try {
                waitForServers.wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




    public void start() {
        if ( thread == null ) {
            if ( servers.size() == 0 ) {
                try {
                    discovery.startDiscovery();
                    SignalkTcpClient.this.updateStatus("Service Discovery started ");

                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("Starting");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("Running");
                    while (running) {
                        SignalKServer server = getServer();
                        if (server != null) {
                            try {
                                SignalkTcpClient.this.updateStatus("Trying server " + server.getUrl());
                                log.info("Server {}  ", server);
                                connect(server);
                            } catch (Exception ex) {
                                log.error("Discovery Failed ", ex.getMessage());
                                log.info("Sleep 5s");
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    log.info("Not Running");
                }
            });
            running = true;
            thread.start();
        }
    }

    public void stop() {
        discovery.endDiscovery();
        SignalkTcpClient.this.updateStatus("Service Discovery stopped ");
        log.info("Stopping");
        running = false;
        thread.interrupt();
        log.info("Stoped");
    }

    private void connect(SignalKServer server) {
        Socket socket = null;
        try {
            String host = server.getHost();
            long port = server.getPort();
            String url = server.getUrl();
            InetAddress address = InetAddress.getByName(host);

            nextFetch = 0;
            if (!fetchState(url)) {
                SignalkTcpClient.this.updateStatus(" No server at " + url);
                server.failed();
                return;
            }
            ;
            SignalkTcpClient.this.updateStatus(" Connecting to " + address + " " + port);

            socket = new Socket();
            socket.connect(new InetSocketAddress(address, (int) port), 5000);
            socket.setSoTimeout(30000); // allow 30s of no data
            SignalkTcpClient.this.updateStatus(" Connected to " + address + " " + port);
            SignalkTcpClient.this.updateStatus(" Fetched state from " + url);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JSONParser parser = new JSONParser();

            while (fetchState(url)) {
                String line = null;
                try {
                    line = in.readLine();
                } catch (SocketTimeoutException e) {
                    log.debug(e.getMessage(), e);
                }
                if (line != null) {
                    try {
                        JSONObject message = (JSONObject) parser.parse(line);
                        List<Map<String, Object>> updates = (List<Map<String, Object>>) message.get("updates");
                        if (updates != null) {
                            for (Map<String, Object> update : updates) {
                                String timestamp = (String) update.get("timestamp");
                                List<Map<String, Object>> values = (List<Map<String, Object>>) update.get("values");
                                if (values != null) {
                                    for (Map<String, Object> value : values) {
                                        value.put("timestamp", timestamp);
                                        store.updateFromServer(value);
                                    }
                                }
                            }
                        }

                    } catch (ParseException e) {
                        log.debug(e.getMessage(), e);
                    }
                }
            }
            socket.close();
            socket = null;
        } catch (Exception e) {
            server.failed();
            SignalkTcpClient.this.updateStatus("Connection Failed closing socket");
            log.error("Failed closing socket {} ", e.getMessage());
            log.debug(e.getMessage(), e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    log.error("Failed closing socket (2) ", e1);
                }
            }
        }
    }

    private boolean fetchState(String url) {
        if (!running) {
            return false;
        }
        if ( url != null) {
            if (nextFetch < System.currentTimeMillis()) {
                nextFetch = System.currentTimeMillis()+30000;
                return httpClient.fetch(url);
            }
        }
        return true;
    }


    private class SignalKServer {
        private String id;
        private String url = null;
        private String host = null;
        private long port = -1;
        private long lastFail = -1;

        public SignalKServer(String id) {
            this.id = id;
        }

        public SignalKServer(Map<String, Object> configServer) {
            id = (String) configServer.get("host");
            url = (String) configServer.get("url");
            host = (String) configServer.get("host");
            port = (long) configServer.get("port");
        }

        public boolean isId(String id) {
            return this.id.equals(id);
        }

        public String getId() {
            return id;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isComplete() {
            return (url != null && port != -1 && host != null);

        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(long port) {
            this.port = port;
        }

        public boolean isAvailable() {
            return ( isComplete() && lastFail+30000L < System.currentTimeMillis());
        }

        public String getHost() {
            return host;
        }

        public long getPort() {
            return port;
        }

        public String getUrl() {
            return url;
        }

        public void failed() {
            lastFail = System.currentTimeMillis();
        }
    }
}