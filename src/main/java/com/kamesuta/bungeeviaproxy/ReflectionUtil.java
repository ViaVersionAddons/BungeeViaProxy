package com.kamesuta.bungeeviaproxy;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static com.kamesuta.bungeeviaproxy.BungeeViaProxy.logger;

public final class ReflectionUtil {

    public static Field bungeeServerInfoSocketAddressField;

    public static Class<?> bungeeServerInfoClass;

    static {
        try {
            bungeeServerInfoClass = Class.forName("net.md_5.bungee.BungeeServerInfo");
            bungeeServerInfoSocketAddressField = bungeeServerInfoClass.getDeclaredField("socketAddress");
            bungeeServerInfoSocketAddressField.setAccessible(true);
        } catch (final Exception e) {
            Logger.getLogger("ReflectionUtil").severe("We have ugly bungeecord implementation right here ._.");
        }
    }

    private ReflectionUtil() {
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
