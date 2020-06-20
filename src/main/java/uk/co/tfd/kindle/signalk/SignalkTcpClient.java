package uk.co.tfd.kindle.signalk;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 19/06/2020.
 */
public class SignalkTcpClient extends StatusUpdates implements ServiceListener {
    private static final Logger log = LoggerFactory.getLogger(SignalkTcpClient.class);
    public static final String SIGNALK_TCP_TCP_LOCAL = "_signalk-tcp._tcp.local.";
    private JmDNS jmdns;
    private final Data.Store store;
    private boolean stayConnected;
    private boolean running;

    public SignalkTcpClient(Data.Store store) throws IOException {
        this.store = store;
    }

    public void startDiscovery() throws IOException {
        InetAddress in = null;

        // find the lowest numbered ingerface that supports multicast and is up.
        // this is on the basis that default interfaces will be first.
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        interfaces.sort(new Comparator<NetworkInterface>() {
            @Override
            public int compare(NetworkInterface o1, NetworkInterface o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        for (NetworkInterface intf : interfaces) {
            log.debug("Interface {} ", intf);
            if ( intf.isUp() && intf.supportsMulticast() && !intf.isLoopback() ) {
                String name = intf.getName();
                // only use names that are expected to be connected to a valid network.
                if (name.startsWith("en") || name.startsWith("wlan") || name.startsWith("eth")) {
                    for ( InterfaceAddress addr : intf.getInterfaceAddresses()) {
                        InetAddress inaddr = addr.getAddress();
                        if (inaddr instanceof Inet4Address) {
                            in = inaddr;
                            this.updateStatus("Discovering Signalk Server on "+in+" "+name);
                            break;
                        }
                    }
                }
                if ( in != null ) {
                    break;
                }
            }

        }
        if ( in == null) {
            in = InetAddress.getLocalHost();
            this.updateStatus("Discovering Signalk Server default interface "+in);
        }

        jmdns = JmDNS.create(in);

        jmdns.addServiceListener(SIGNALK_TCP_TCP_LOCAL, this);

    }

    public void endDiscovery() {
        jmdns.removeServiceListener(SIGNALK_TCP_TCP_LOCAL, this);
        try {
            jmdns.close();
            jmdns = null;
        } catch (IOException e) {
            log.error("Failed to end discovery",e);
        }

    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        log.debug("Service added {} ", serviceEvent);
    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        log.debug("Service removed {} ",serviceEvent);

    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {

        if ( !running ) {
            log.debug("Connecting to {} ",serviceEvent);
            try {
                running = true;
                endDiscovery();
                log.debug("Removed Listener");

                connect(serviceEvent);
                log.debug("Connect Done");

            } catch (Throwable t) {
                log.debug("Connect Failed",t);
            }
        } else {
            log.debug("Already Connected, ignoring {} ",serviceEvent);
        }
/*
[ServiceEventImpl@1041224902
	name: 'x43543-2' type: '_signalk-tcp._tcp.local.' info: '[ServiceInfoImpl@931430545 name: 'x43543-2._signalk-tcp._tcp.local.' address: '/192.168.1.134:8375 /fe80:0:0:0:fa1e:dfff:fee4:f32a:8375 ' status: 'NO DNS state: probing 1 task: null' is persistent, has data
	self: urn:mrn:signalk:uuid:c0d79334-4e25-4245-8892-54e8ccc80222
	roles: master, main
	vname: Lona
	txtvers: 1
	vuuid: urn:mrn:signalk:uuid:c0d79334-4e25-4245-8892-54e8ccc80222
	swvers: 1.18.0
	swname: signalk-server]']
 */
    }


    private void connect(ServiceEvent serviceEvent) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    InetAddress address = serviceEvent.getInfo().getInet4Addresses()[0];
                    int port = serviceEvent.getInfo().getPort();
                    SignalkTcpClient.this.updateStatus(" Connecting to " + address + " " + port);

                    socket = new Socket(address, port);
                    SignalkTcpClient.this.updateStatus(" Connected to " + address + " " + port);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    JSONParser parser = new JSONParser();
                    stayConnected = true;
                    while(stayConnected ) {
                        String line = in.readLine();
                        if ( line == null) {
                            stayConnected = false;
                        } else {
                            try {
                                JSONObject message = (JSONObject) parser.parse(line);
                                List<Map<String, Object>> updates = (List<Map<String, Object>>) message.get("updates");
                                if ( updates != null ) {
                                    for (Map<String,Object> update: updates ) {
                                        String timestamp = (String) update.get("timestamp");
                                        List<Map<String, Object>> values = (List<Map<String, Object>>) update.get("values");
                                        if ( values != null ) {
                                            for ( Map<String, Object> value : values) {
                                                value.put("timestamp", timestamp);
                                                store.updateFromServer(value);
                                            }
                                        }
                                    }
                                }

                            } catch (ParseException e) {

                                log.debug(e.getMessage(),e);
                            }
                        }
                    }
                    socket.close();
                    socket = null;
                } catch (Exception e) {
                    SignalkTcpClient.this.updateStatus("Connection Failed closing socket");
                    log.error("Failed closing socket ", e);


                    if ( socket != null ) {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            log.error("Failed closing socket (2) ", e1);
                        }
                    }

                } finally {
                    running = false;

                    try {
                        startDiscovery();
                    } catch (IOException e) {
                        log.error("Failed starting discovery ",e);
                    }
                    log.debug("Listening again");

                }
            }
        });
        t.start();
    }


}