package com.probal.todoapp.exception;

public class TodoItemNotFountException extends Exception {
    public TodoItemNotFountException(String errorMessage) {
        super(errorMessage);
    }
}
