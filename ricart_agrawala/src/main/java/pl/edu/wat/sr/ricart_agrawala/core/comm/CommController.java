package pl.edu.wat.sr.ricart_agrawala.core.comm;

import pl.edu.wat.sr.ricart_agrawala.core.res.ResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class CommController {
    private final ResourceController resourceController;
    private final SocketHandler socketHandler;
    private final ArrayList<Integer> remotePorts ;

    public CommController() {
        socketHandler = new SocketHandler();
        remotePorts = new ArrayList<>();
        resourceController = ResourceController.getInstance();
    }

    public void setLocalAddress(String address) {
        socketHandler.setAddress(address);
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("%s : %s",
                resourceController.getText("log_info_listen_address_changed"),
                address));
    }
    public String getLocalAddress() {
        return socketHandler.getAddress();
    }
    public void setLocalPort(Integer port) throws AccessDeniedException, InvalidParameterException {
        if (isPortAvailable(port)) {
            socketHandler.setPort(port);
            LogController.getInstance().logInfo(this.getClass().getName(), String.format("%s : %d",
                    resourceController.getText("log_info_listen_port_changed"),
                    port));
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
            LogController.getInstance().logInfo(this.getClass().getName(), String.format("%s : %d",
                    resourceController.getText("log_info_remote_port_added"),
                    port));
        }
    }
    public void clearRemotePorts() {
        remotePorts.clear();
        LogController.getInstance().logInfo(this.getClass().getName(),
                resourceController.getText("log_info_remote_ports_list_cleared"));
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
        logger.logInfo(this.getClass().getName(), String.format("%s : %s:%d ...",
                resourceController.getText("log_info_port_availability_check"),
                address,
                port));
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress, 1000);
            logger.logError(this.getClass().getName(), String.format("(%s:%d) : %s!",
                    address,
                    port,
                    resourceController.getText("log_error_port_unavailable")));
            return false;
        } catch (Exception e) {
            logger.logInfo(this.getClass().getName(), String.format("(%s:%d) : %s.",
                    address,
                    port,
                    resourceController.getText("log_info_port_available")));
            return true;
        }
    }
}
