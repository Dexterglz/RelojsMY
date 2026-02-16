package com.example.myhealthlife.domain.util;

public class ResourceWrapper<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    public final T data;
    public final String message;

    private ResourceWrapper(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ResourceWrapper<T> loading() {
        return new ResourceWrapper<>(Status.LOADING, null, null);
    }

    public static <T> ResourceWrapper<T> success(T data) {
        return new ResourceWrapper<>(Status.SUCCESS, data, null);
    }

    public static <T> ResourceWrapper<T> error(String msg) {
        return new ResourceWrapper<>(Status.ERROR, null, msg);
    }
}


