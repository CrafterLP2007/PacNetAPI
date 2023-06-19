package de.crafterlp.net;

import java.net.InetSocketAddress;

public class RemoteClient {

    private String identifier;
    private InetSocketAddress inetSocketAddress;


    public RemoteClient(String identifier, InetSocketAddress inetSocketAddress) {
        this.identifier = identifier;
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getIdentifier() {
        return identifier;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public String getHost() {
        return inetSocketAddress.getHostName();
    }

    public int getPort() {
        return inetSocketAddress.getPort();
    }
}
