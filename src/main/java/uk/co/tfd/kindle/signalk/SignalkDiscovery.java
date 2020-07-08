package uk.co.tfd.kindle.signalk;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
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
public class SignalkDiscovery {
    private static final Logger log = LoggerFactory.getLogger(SignalkDiscovery.class);
    public static final String SIGNALK_TCP_TCP_LOCAL = "_signalk-tcp._tcp.local.";
    public static final String SIGNALK_HTTP_TCP_LOCAL = "_signalk-http._tcp.local.";
    private final SignalkTcpClient client;
    private JmDNS jmdns;
    private ServiceListener tcpListener;
    private ServiceListener httpListener;

    public SignalkDiscovery( SignalkTcpClient client) {
        this.client = client;
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
            if (intf.isUp() && intf.supportsMulticast() && !intf.isLoopback()) {
                String name = intf.getName();
                // only use names that are expected to be connected to a valid network.
                if (name.startsWith("en") || name.startsWith("wlan") || name.startsWith("eth")) {
                    for (InterfaceAddress addr : intf.getInterfaceAddresses()) {
                        InetAddress inaddr = addr.getAddress();
                        if (inaddr instanceof Inet4Address) {
                            in = inaddr;
                            log.info("Discovering Signalk Server on {} {} ", in, name);
                            break;
                        }
                    }
                }
                if (in != null) {
                    break;
                }
            }

        }
        if (in == null) {
            in = InetAddress.getLocalHost();
            log.info("Discovering Signalk Server default interface {} ",in);
        }

        jmdns = JmDNS.create(in);

        tcpListener = new ServiceListener() {

            @Override
            public void serviceAdded(ServiceEvent serviceEvent) {

            }

            @Override
            public void serviceRemoved(ServiceEvent serviceEvent) {
                log.info("Remove Http Info {} ", serviceEvent.getInfo());
                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                client.removeServer(host);
            }

            @Override
            public void serviceResolved(ServiceEvent serviceEvent) {
                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                client.addTcp(host, host, info.getPort());
                log.debug("TCP Info {} ", info);
            }
        };
        httpListener = new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent serviceEvent) {

            }

            @Override
            public void serviceRemoved(ServiceEvent serviceEvent) {

                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                log.debug("Remove Http Info {} ", info);
                client.removeServer(host);

            }

            @Override
            public void serviceResolved(ServiceEvent serviceEvent) {
                ServiceInfo info = serviceEvent.getInfo();
                String host = info.getHostAddresses()[0];
                try {
                    URL u = new URL("http",host, info.getPort(), "");
                    client.addHttp(host,u.toString());
                    log.info("Http Info {} ", info);
                } catch (MalformedURLException e) {
                    log.error(e.getMessage(), e);
                }

            }
        };

        jmdns.addServiceListener(SIGNALK_TCP_TCP_LOCAL, tcpListener);
        jmdns.addServiceListener(SIGNALK_HTTP_TCP_LOCAL, httpListener);
    }

    public void endDiscovery() {
        jmdns.removeServiceListener(SIGNALK_TCP_TCP_LOCAL, tcpListener);
        jmdns.removeServiceListener(SIGNALK_HTTP_TCP_LOCAL, httpListener);
        try {
            jmdns.close();
            jmdns = null;
        } catch (IOException e) {
            log.error("Failed to end discovery", e);
        }
    }



}