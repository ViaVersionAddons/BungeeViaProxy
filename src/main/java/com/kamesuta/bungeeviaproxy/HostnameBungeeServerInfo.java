package com.kamesuta.bungeeviaproxy;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

public class HostnameBungeeServerInfo extends BungeeServerInfo {

    public HostnameBungeeServerInfo(String name, SocketAddress socketAddress, String motd, boolean restricted) {
        super(name, socketAddress, motd, restricted);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServerInfo)) return false;

        InetSocketAddress address = this.getAddress();
        @SuppressWarnings("deprecation")
        InetSocketAddress objAddress = ((ServerInfo) obj).getAddress();

        if (address == null && objAddress == null) return true;
        if (address == null || objAddress == null) return false;

        // BungeeServerInfo is treated as the same server if the resolved IP addresses are the same,
        // so treat them as different servers if the hostnames are different.
        return Objects.equals(address.getHostName(), objAddress.getHostName()) && address.getPort() == objAddress.getPort();
    }

    @Override
    public int hashCode() {
        InetSocketAddress address = this.getAddress();
        if (address == null) return 0;

        int result = 17;
        result = 31 * result + address.getPort();
        if (address.getHostName() != null) {
            result = 31 * result + address.getHostName().hashCode();
        }
        return result;
    }

}
