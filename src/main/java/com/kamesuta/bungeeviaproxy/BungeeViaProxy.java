package com.kamesuta.bungeeviaproxy;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class BungeeViaProxy extends Plugin implements Listener {

    public static final Logger logger = LogManager.getLogManager().getLogger("BungeeViaProxy");
    public static final String IDENTIFIER = ".viaproxy.";

    @Override
    public void onEnable() {
        // Plugin startup logic
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);

        // In Bungeecord, even if the hostnames are different, the equals method returns true if the resolved IP addresses are the same.
        // This results in the error "You are already connected to this server!".
        // To avoid this, we use unresolved InetSocketAddress objects so that different hostnames are treated as distinct servers.
        ProxyServer.getInstance().getServers().values().forEach(serverInfo -> {
            // Convert address to unresolved address
            SocketAddress socketAddress = serverInfo.getSocketAddress();
            if (socketAddress instanceof InetSocketAddress) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                // Only convert if the address is a ViaProxy address
                if (inetSocketAddress.getHostString().contains(IDENTIFIER) && ReflectionUtil.isBungeeSeverInfo(serverInfo)) {
                    ReflectionUtil.setBungeeServerInfoSocketAddress(
                            serverInfo,
                            InetSocketAddress.createUnresolved(inetSocketAddress.getHostString(), inetSocketAddress.getPort())
                    );
                }
            }
        });
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        // Get the target server address
        ServerInfo serverInfo = event.getTarget();
        SocketAddress socketAddress = serverInfo.getSocketAddress();
        String host = socketAddress.toString();

        // When "wildcard-domain-handling: PUBLIC" is enabled in ViaProxy, the hostname specified in the Bungeecord config
        // cannot be retrieved on the ViaProxy side. This makes it impossible to differentiate the target servers.
        // To address this, any address containing ".viaproxy." is passed directly to the target server.
        if (host.contains(IDENTIFIER)) {
            PendingConnection con = event.getPlayer().getPendingConnection();
            if (ReflectionUtil.isInitialHandler(con)) {
                ReflectionUtil.setInitialHandlerHandshakeHost(con, host);
            }
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        // Reverse the changes made in onServerConnect after the connection is established
        PendingConnection con = event.getPlayer().getPendingConnection();
        String host = con.getVirtualHost().getHostString();
        if (host.contains(IDENTIFIER) && ReflectionUtil.isInitialHandler(con)) {
            ReflectionUtil.setInitialHandlerHandshakeHost(con, host);
        }
    }

}
