package pl.edu.wat.sr.ricart_agrawala.core.comm;

import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.security.Permission;
import java.util.ArrayList;

public class CommController {
    private static CommController instance;
    private final SocketHandler socketHandler;
    private final ArrayList<Integer> remotePorts ;

    private CommController() {
        socketHandler = new SocketHandler();
        remotePorts = new ArrayList<>();
    }

    public static CommController getInstance() {
        if (instance == null) {
            instance = new CommController();
        }
        return instance;
    }

    public void setLocalAddress(String address) {
        socketHandler.setAddress(address);
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("Listening address set on : %s", address));
    }
    public String getLocalAddress() {
        return socketHandler.getAddress();
    }
    public void setLocalPort(Integer port) throws AccessDeniedException, InvalidParameterException {
        if (isPortAvailable(port)) {
            socketHandler.setPort(port);
            LogController.getInstance().logInfo(this.getClass().getName(), String.format("Listening port set on : %d", port));
        } else {
            throw new AccessDeniedException("Port (" + port + ") already in use!");
        }
    }
    public Integer getLocalPort() {
        return socketHandler.getPort();
    }

    public void addRemotePort(Integer port) {
        if(!remotePorts.contains(port)) {
            remotePorts.add(port);
            LogController.getInstance().logInfo(this.getClass().getName(), String.format("Remote port added: %d", port));
        }
    }
    public void clearRemotePorts() {
        remotePorts.clear();
        LogController.getInstance().logInfo(this.getClass().getName(), "Remote ports list cleared");
    }
    public ArrayList<Integer> getRemotePorts() {
        return remotePorts;
    }

    private boolean isPortAvailable(int port) throws InvalidParameterException {
        String address = getLocalAddress();
        if (address == null) {
            throw new InvalidParameterException("No specified interface address!");
        }

        LogController logger = LogController.getInstance();
        logger.logInfo(this.getClass().getName(), String.format("Checking port availability at : %s:%d ...", address, port));
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress, 1000);
            logger.logError(this.getClass().getName(), String.format("Port (%s:%d) already in use!", address, port));
            return false;
        } catch (Exception e) {
            logger.logInfo(this.getClass().getName(), String.format("Port (%s:%d) is available.", address, port));
            return true;
        }
    }
}
