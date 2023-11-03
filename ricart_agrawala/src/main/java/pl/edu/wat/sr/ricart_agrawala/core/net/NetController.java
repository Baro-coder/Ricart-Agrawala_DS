package pl.edu.wat.sr.ricart_agrawala.core.net;

import pl.edu.wat.sr.ricart_agrawala.StringsResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

import java.net.*;
import java.security.KeyException;
import java.util.*;

public class NetController {
    private final StringsResourceController resourceController;
    private final ArrayList<NetInterface> interfaces = new ArrayList<>();
    private NetInterface activeInterface;
    private Integer updateInterval = -1;

    public NetController() {
        resourceController = StringsResourceController.getInstance();
    }

    public static byte[] getNextAddress(byte[] actualAddressBytes) {
        if (actualAddressBytes.length != 4) {
            throw new IllegalArgumentException("Address should by 4 bytes long!");
        }

        for (int i = 3; i >= 0; i--) {
            if (actualAddressBytes[i] != -1) {
                actualAddressBytes[i]++;
                break;
            } else {
                actualAddressBytes[i] = 0;
            }
        }

        return actualAddressBytes;
    }

    public static byte[] getPreviousAddress(byte[] actualAddressBytes) {
        if (actualAddressBytes.length != 4) {
            throw new IllegalArgumentException("Address should by 4 bytes long!");
        }

        for (int i = 3; i >= 0; i--) {
            if (actualAddressBytes[i] != 0) {
                actualAddressBytes[i]--;
                break;
            } else {
                actualAddressBytes[i] = -1;
            }
        }

        return actualAddressBytes;
    }

    public void update() {
        LogController logger = LogController.getInstance();
        logger.logInfo(this.getClass().getName(), "Updating network interfaces list...");
        interfaces.clear();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                int lastIndex = interfaceAddresses.size() - 1;
                if (lastIndex < 0) {
                    continue;
                }

                // Interface name
                String name = networkInterface.getName();

                // Interface address, netmask
                InetAddress interfaceInetAddress = interfaceAddresses.get(lastIndex).getAddress();
                if (interfaceInetAddress.isLoopbackAddress() || interfaceInetAddress.isLinkLocalAddress()) {
                    continue;
                }
                String address = interfaceInetAddress.getHostAddress();
                byte[] addressBytes = interfaceInetAddress.getAddress();
                int hashCode = interfaceInetAddress.hashCode();
                short subnetPrefixLength = interfaceAddresses.get(lastIndex).getNetworkPrefixLength();

                // Broadcast address
                InetAddress broadcastInetAddress = interfaceAddresses.get(lastIndex).getBroadcast();
                String broadcast = broadcastInetAddress.getHostAddress();
                byte[] broadcastBytes = broadcastInetAddress.getAddress();

                try {
                    NetInterface newNetInterface = new NetInterface(name, address, addressBytes, hashCode, broadcast, broadcastBytes, subnetPrefixLength);
                    interfaces.add(newNetInterface);
                } catch (UnknownHostException | IllegalArgumentException ignored) {
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } finally {
            logger.logInfo(this.getClass().getName(), String.format("Detected interfaces count : %d", interfaces.size()));
            for (NetInterface netInterface : interfaces) {
                logger.logDebug(this.getClass().getName(), " - " + netInterface);
                logger.logDebug(this.getClass().getName(), "     - subnet hosts : " + netInterface.getSubnetHostsCount());
                logger.logDebug(this.getClass().getName(), "     - host min : " + netInterface.getSubnetHostMin());
                logger.logDebug(this.getClass().getName(), "     - host max : " + netInterface.getSubnetHostMax());
            }
        }
    }

    public ArrayList<NetInterface> getNetInterfaces() {
        return interfaces;
    }

    public NetInterface getNetInterfaceByName(String name) throws KeyException {
        for (NetInterface netInterface : interfaces) {
            if (Objects.equals(netInterface.getName(), name)) {
                return netInterface;
            }
        }
        throw new KeyException("No such interface with this name : " + name);
    }

    public NetInterface getNetInterfaceByAddress(String address) throws KeyException {
        for (NetInterface netInterface : interfaces) {
            if (Objects.equals(netInterface.getAddress(), address)) {
                return netInterface;
            }
        }
        throw new KeyException("No such interface with this address : " + address);
    }

    public NetInterface getNetInterfaceBySubnet(String subnet) throws KeyException {
        for (NetInterface netInterface : interfaces) {
            if (Objects.equals(netInterface.getSubnet(), subnet)) {
                return netInterface;
            }
        }
        throw new KeyException("No such interface in this subnet : " + subnet);
    }

    public ArrayList<String> getNetInterfacesNames() {
        ArrayList<String> names = new ArrayList<>();
        for (NetInterface netInterface : interfaces) {
            names.add(netInterface.getName());
        }
        return names;
    }

    public void setActiveInterface(NetInterface netInterface) {
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("Network interface switched to : %s", netInterface.getName()));
        activeInterface = netInterface;
    }

    public NetInterface getActiveInterface() {
        return activeInterface;
    }

    public void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
    }
    public Integer getUpdateInterval() {
        return updateInterval;
    }
}