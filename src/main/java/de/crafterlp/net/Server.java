package de.crafterlp.net;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public abstract class Server {

    protected int port;
    protected boolean secureMode;
    protected boolean debug;
    protected boolean stopped;
    protected ServerSocket server;
    protected Thread listeningThread;
    protected String LOGIN_ID = "_LOGIN_";
    protected Map<RemoteClient, Socket> clients = new HashMap<>();
    protected Socket tempSocket;

    public Server(int port) {
        this(port, false, true);
    }

    public Server(int port, boolean secureMode) {
        this(port, secureMode, true);
    }

    public Server(int port, boolean secureMode, boolean debug) {
        this.port = port;
        this.secureMode = secureMode;
        this.debug = debug;

        if (secureMode) {
            System.setProperty("javax.net.ssl.keyStore", "ssc.store");
            System.setProperty("javax.net.ssl.keyStorePassword", "PacNetAPI");
        }
        preStart();
    }

    public abstract void preStart();
    public abstract void handlePacket(DataPackage dataPackage);

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void start() {
        this.stopped = false;
        this.server = null;

        try {
            if (this.secureMode) {
                this.server = SSLServerSocketFactory.getDefault().createServerSocket(this.port);
            } else {
                this.server = new ServerSocket(this.port);
            }
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        log("Waiting for connection" + (secureMode ? " using SSL..." : "..."));
        if (this.listeningThread == null && !stopped && server != null) {
            listeningThread = new Thread(() -> {
                while (!Thread.interrupted() && !stopped && server != null) {
                    try {
                        tempSocket = server.accept();

                        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(tempSocket.getInputStream()));
                        Object raw = objectInputStream.readObject();

                        if (raw instanceof DataPackage) {
                            DataPackage dataPackage = (DataPackage) raw;

                            log("New message received from a Client: " + dataPackage.id());

                            if (dataPackage.id().equalsIgnoreCase(LOGIN_ID)) {
                                log("New Client connected!");
                                RemoteClient remoteClient = new RemoteClient((String) dataPackage.get(1), (InetSocketAddress) dataPackage.get(2));
                                //registerClient(remoteClient.getIdentifier(), remoteClient.getInetSocketAddress());
                                registerClient(remoteClient, tempSocket);
                                handlePacket(dataPackage);
                            } else {
                                handlePacket(dataPackage);
                            }
                        }

                        if (tempSocket.isClosed()) {
                            break;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            listeningThread.start();
        }
    }

    public void registerClient(RemoteClient remoteClient, Socket socket) {
        clients.put(remoteClient, socket);
        onClientRegistered(remoteClient, socket);
    }

    public void sendBroadCast(DataPackage dataPackage) {
        for (RemoteClient remoteClient : clients.keySet()) {
            sendMessage(remoteClient.getIdentifier(), dataPackage);
        }
    }

    public void sendMessage(String identifier, DataPackage dataPackage) {
        RemoteClient remoteClient = getClientByIdentifier(identifier);
        if (remoteClient != null) {
            Socket sendSocket = clients.get(remoteClient);
            try {
                ObjectOutputStream out = new ObjectOutputStream(sendSocket.getOutputStream());
                out.writeObject(dataPackage);
                out.flush();

                onMessageSend(identifier, dataPackage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log("Cant find client with identifier: " + identifier);
        }
    }

    private RemoteClient getClientByIdentifier(String identifier) {
        for (RemoteClient client : clients.keySet()) {
            if (client.getIdentifier().equals(identifier)) {
                return client;
            }
        }
        return null;
    }

    public void onClientRegistered(RemoteClient remoteClient, Socket socket) {
        //
    }

    public void onMessageSend(String identifier, DataPackage dataPackage) {
        //
    }

    public void log(String message) {
        if (debug) {
            System.out.println("[Server]: " + message);
        }
    }
}
