package com.example.whoami.exception;

public class InvalidRoomLinkException extends RuntimeException {
    public InvalidRoomLinkException() {
        super();
    }

    public InvalidRoomLinkException(String message) {
        super(message);
    }

    public InvalidRoomLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
