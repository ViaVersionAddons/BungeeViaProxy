package com.kamesuta.bungeeviaproxy;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static com.kamesuta.bungeeviaproxy.BungeeViaProxy.logger;

public final class ReflectionUtil {

    public static Field initialHandlerHandshakeField, handshakeHostField, bungeeServerInfoSocketAddressField;

    public static Class<?> initialHandlerClass, handshakeClass, bungeeServerInfoClass;

    static {
        try {
            initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");
            initialHandlerHandshakeField = initialHandlerClass.getDeclaredField("handshake");
            initialHandlerHandshakeField.setAccessible(true);
            handshakeClass = Class.forName("net.md_5.bungee.protocol.packet.Handshake");
            handshakeHostField = handshakeClass.getDeclaredField("host");
            handshakeHostField.setAccessible(true);
            bungeeServerInfoClass = Class.forName("net.md_5.bungee.BungeeServerInfo");
            bungeeServerInfoSocketAddressField = bungeeServerInfoClass.getDeclaredField("socketAddress");
            bungeeServerInfoSocketAddressField.setAccessible(true);
        } catch (final Exception e) {
            Logger.getLogger("ReflectionUtil").severe("We have ugly bungeecord implementation right here ._.");
        }
    }

    private ReflectionUtil() {
    }

    public static boolean isInitialHandler(Object obj) {
        return initialHandlerClass.isAssignableFrom(obj.getClass());
    }

    public static void setInitialHandlerHandshakeHost(Object obj, String host) {
        try {
            Object handshake = initialHandlerHandshakeField.get(obj);
            handshakeHostField.set(handshake, host);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to set handshake host", e);
        }
    }

    public static boolean isBungeeSeverInfo(Object obj) {
        return bungeeServerInfoClass.isAssignableFrom(obj.getClass());
    }

    public static void setBungeeServerInfoSocketAddress(Object obj, InetSocketAddress address) {
        try {
            bungeeServerInfoSocketAddressField.set(obj, address);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to set server info address", e);
        }
    }

}
