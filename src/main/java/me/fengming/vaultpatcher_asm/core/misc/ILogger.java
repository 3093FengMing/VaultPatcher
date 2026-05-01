package me.fengming.vaultpatcher_asm.core.misc;

public class ILogger {
    private Object impl;
    private String PREFIX="";

    public ILogger() {
        try {
            Class<?> logManager=Class.forName("org.apache.logging.log4j.LogManager");
            impl=logManager.getMethod("getLogger", String.class).invoke(null,"VaultPatcher");
        } catch (Throwable e) {
            impl=null;
        }
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            PREFIX="[VaultPatcher] ";
        } catch (Throwable ignored) {}
    }

    public void info(String msg) {
        try {
            impl.getClass().getMethod("info", String.class).invoke(impl, this.PREFIX+msg);
        } catch (Throwable e) {
            System.out.println("[VaultPatcher] "+msg);
        }
    }

    public void info(String msg, Object... args) {
        try {
            impl.getClass().getMethod("info", String.class, Object[].class).invoke(impl, this.PREFIX+msg, args);
        } catch (Throwable e) {
            for (Object arg : args) {
                msg = msg.replaceFirst("\\{}", String.valueOf(arg));
            }
            System.out.println("[VaultPatcher] "+msg);
        }
    }

    public void warn(String msg) {
        try {
            impl.getClass().getMethod("warn", String.class).invoke(impl, this.PREFIX+msg);
        } catch (Throwable e) {
            System.out.println("[VaultPatcher] "+msg);
        }
    }

    public void warn(String msg, Object... args) {
        try {
            impl.getClass().getMethod("warn", String.class, Object[].class).invoke(impl, this.PREFIX+msg, args);
        } catch (Throwable e) {
            for (Object arg : args) {
                msg = msg.replaceFirst("\\{}", String.valueOf(arg));
            }
            System.out.println("[VaultPatcher] "+msg);
        }
    }

    public void error(String msg) {
        try {
            impl.getClass().getMethod("error", String.class).invoke(impl, this.PREFIX+msg);
        } catch (Throwable e) {
            System.err.println("[VaultPatcher] "+msg);
        }
    }

    public void error(String msg, Object... args) {
        try {
            impl.getClass().getMethod("error", String.class, Object[].class).invoke(impl, this.PREFIX+msg, args);
        } catch (Throwable e) {
            for (Object arg : args) {
                msg = msg.replaceFirst("\\{}", String.valueOf(arg));
            }
            System.err.println("[VaultPatcher] "+msg);
        }
    }
}
