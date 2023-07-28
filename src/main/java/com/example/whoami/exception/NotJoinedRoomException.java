package com.example.whoami.exception;

public class NotJoinedRoomException extends RuntimeException {
    public NotJoinedRoomException() { super(); }

    public NotJoinedRoomException(String message) {
        super(message);
    }

    public NotJoinedRoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
