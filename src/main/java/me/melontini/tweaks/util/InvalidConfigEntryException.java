package me.melontini.tweaks.util;

public class InvalidConfigEntryException extends RuntimeException {
    public InvalidConfigEntryException(String message) {
        super("[m-tweaks] " + message);
    }

    public InvalidConfigEntryException(String message, Throwable throwable) {
        super("[m-tweaks] " + message, throwable);
    }
}
