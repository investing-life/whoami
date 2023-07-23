package com.example.whoami.exception;

public class DuplicateRoomMemberException extends RuntimeException {
    public DuplicateRoomMemberException() {
        super();
    }

    public DuplicateRoomMemberException(String message) {
        super(message);
    }

    public DuplicateRoomMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}
