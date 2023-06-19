package de.crafterlp.net;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public abstract class Client {

    protected String host;
    protected int port;
    protected boolean debug;
    protected boolean stopped;
    protected Socket socket;
    protected InetSocketAddress address;
    protected String identifier;
    protected Thread listeningThread;

    public Client(String host, int port, String identifier) {
        this(host, port, identifier, true);
    }

    public Client(String host, int port, String identifier, boolean debug) {
        this.host = host;
        this.port = port;
        this.identifier = identifier;
        this.debug = debug;

        address = new InetSocketAddress(host, port);

        socket = new Socket();

        preStart();
    }

    public abstract void preStart();
    public abstract void handlePacket(DataPackage dataPackage);

    public void start() {
        try {
            socket.connect(address, 30);
            log("Connected to: " + socket.getRemoteSocketAddress());
            onConnected(socket.getInetAddress());

            login();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        try {
            log("Logging in...");
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataPackage loginPackage = new DataPackage("_LOGIN_", identifier, address);
            loginPackage.sign("_LOGIN_");
            out.writeObject(loginPackage);
            out.flush();
            log("Successfully logged in.");

            onLoggedIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(DataPackage dataPackage) {
        if (socket == null && !socket.isConnected()) {
            log("Client with identifier: " + identifier + " is not connected to a server!");
            return;
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(dataPackage);
            out.flush();

            onSendMessage(dataPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() {
        if (this.listeningThread == null && !stopped && socket != null) {
            listeningThread = new Thread(() -> {
                while (!Thread.interrupted() && !stopped && socket != null) {
                    try {
                        if (socket.isClosed()) {
                            break;
                        }

                        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                        Object raw = objectInputStream.readObject();

                        if (raw instanceof DataPackage) {
                            DataPackage dataPackage = (DataPackage) raw;

                            log("New message received from Server: " + dataPackage.id());

                            handlePacket(dataPackage);

                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            listeningThread.start();
        }
    }

    public void onConnected(InetAddress address) {
        //
    }

    public void onLoggedIn() {
        //
    }

    public void onSendMessage(DataPackage dataPackage) {
        //
    }

    public void log(String message) {
        if (debug) {
            System.out.println("[Client]: " + message);
        }
    }
}
