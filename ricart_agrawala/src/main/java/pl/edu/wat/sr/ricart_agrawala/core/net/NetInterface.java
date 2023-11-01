package pl.edu.wat.sr.ricart_agrawala.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static java.lang.Math.pow;

public class NetInterface {
    private final String name;
    private final String address;
    private final byte[] addressBytes;
    private final int hashCode;
    private final String subnet;
    private final byte[] subnetBytes;
    private final String broadcast;
    private final byte[] broadcastBytes;
    private final short subnetPrefixLength;
    private final byte[] subnetMaskBytes;
    private final String subnetHostMin;
    private final byte[] subnetHostMinBytes;
    private final String subnetHostMax;
    private final byte[] subnetHostMaxBytes;
    private final long subnetHostsCount;
    private final boolean specified;

    public NetInterface(String name, String address, byte[] addressBytes, int hashCode, String broadcast, byte[] broadcastBytes, short subnetPrefixLength) throws UnknownHostException, IllegalArgumentException {
        specified = true;
        this.name = name;
        this.address = address;
        this.addressBytes = addressBytes;
        this.hashCode = hashCode;
        this.broadcast = broadcast;
        this.broadcastBytes = broadcastBytes;
        this.subnetPrefixLength = subnetPrefixLength;

        subnetHostsCount = (long) pow(2, 32 - subnetPrefixLength) - 2;

        subnetBytes = new byte[4];
        subnetMaskBytes = new byte[4];
        calculateSubnetAddress();
        try {
            subnet = InetAddress.getByAddress(subnetBytes).getHostAddress();
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Cannot get address address from : " + Arrays.toString(subnetBytes));
        }

        subnetHostMinBytes = NetController.getNextAddress(subnetBytes);
        try {
            subnetHostMin = InetAddress.getByAddress(subnetHostMinBytes).getHostAddress();
        } catch (UnknownHostException e){
            throw new UnknownHostException("Cannot get address address from : " + Arrays.toString(subnetHostMinBytes));
        }

        subnetHostMaxBytes = NetController.getPreviousAddress(broadcastBytes);
        try {
            subnetHostMax = InetAddress.getByAddress(subnetHostMaxBytes).getHostAddress();
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Cannot get address address from : " + Arrays.toString(subnetHostMaxBytes));
        }
    }

    private NetInterface(String name, String address, byte[] addressBytes, int hashCode, String subnet, byte[] subnetBytes, String broadcast, byte[] broadcastBytes, short subnetPrefixLength, byte[] subnetMaskBytes, boolean specified) {
        this.name = name;
        this.address = address;
        this.addressBytes = addressBytes;
        this.hashCode = hashCode;
        this.subnet = subnet;
        this.subnetBytes = subnetBytes;
        this.subnetMaskBytes = subnetMaskBytes;
        this.broadcast = broadcast;
        this.broadcastBytes = broadcastBytes;
        this.subnetPrefixLength = subnetPrefixLength;
        this.specified = specified;

        this.subnetHostMin = subnet;
        this.subnetHostMinBytes = subnetBytes;
        this.subnetHostMax = subnet;
        this.subnetHostMaxBytes = subnetBytes;
        this.subnetHostsCount = -1;
    }

    public static NetInterface getDefault() {
        String zeroStr = "0.0.0.0";
        byte[] zeroTab = {0x00, 0x00, 0x00, 0x00};
        return new NetInterface("all", zeroStr, zeroTab, 0, zeroStr, zeroTab, zeroStr, zeroTab, (short) 0, zeroTab, false);
    }

    @Override
    public String toString() {
        return String.format("%-12s : %-16s : %-16s : %-16s /%-3s", name, subnet, broadcast, address, subnetPrefixLength);
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public byte[] getAddressBytes() { return addressBytes; }
    public int getHashCode() { return hashCode; }
    public String getSubnet() { return subnet; }
    public byte[] getSubnetBytes() { return subnetBytes; }
    public byte[] getSubnetMaskBytes() { return subnetMaskBytes; }
    public long getSubnetHostsCount() { return subnetHostsCount; }
    public String getBroadcast() { return broadcast; }
    public byte[] getBroadcastBytes() { return broadcastBytes; }
    public short getSubnetPrefixLength() { return subnetPrefixLength; }
    public boolean isSpecified() { return specified; }
    public String getSubnetHostMin() { return subnetHostMin; }
    public byte[] getSubnetHostMinBytes() { return subnetHostMinBytes; }
    public String getSubnetHostMax() { return subnetHostMax; }
    public byte[] getSubnetHostMaxBytes() { return subnetHostMaxBytes; }

    private void calculateSubnetAddress() {
        int subnetMaskInt = (0xFFFFFFFF << (32 - subnetPrefixLength));
        for(int i = 0; i < 4; i++) {
            subnetMaskBytes[i] = (byte)((subnetMaskInt >> (24 - i * 8)) & 0xFF);
            subnetBytes[i] = (byte) (addressBytes[i] & subnetMaskBytes[i]);
        }
    }
}
