package pl.edu.wat.sr.ricart_agrawala.core.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {
    private String address;
    private Integer port;
    private Socket socket;
    private boolean listening;

    public SocketHandler() {
        this.address = "0.0.0.0";
        this.port = 0;
        listening = false;
    }
    public SocketHandler(String address, int port) {
        this.address = address;
        this.port = port;
        listening = false;
    }

    public void runServer() {
        // TODO : Socket listening procedure
        if (!isListening()) {
            socket = new Socket();
            listening = true;
        }
    }
    public void stopServer() {
        // TODO : Socket close hot check
        if (isListening()) {
            try {
                socket.close();
            } catch (IOException ignored) {}
            listening = false;
        }
    }

    public void sendBroadcast() {
        // TODO : Not implemented yet
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() { return address; }
    public void setPort(Integer port) {
        this.port = port;
    }
    public Integer getPort() { return port; }
    public boolean isListening() { return listening; }
}
