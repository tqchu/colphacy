package com.colphacy.exception;

public class BranchHasOperatedException extends RuntimeException {
    public BranchHasOperatedException() {
        super("Chi nhánh này đã đi vào hoạt động!");
    }

    public BranchHasOperatedException(String message) {
        super(message);
    }
}