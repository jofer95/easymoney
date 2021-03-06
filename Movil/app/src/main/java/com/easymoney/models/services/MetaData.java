package com.easymoney.models.services;

/**
 * Created by ulises on 30/12/17.
 */

public class MetaData<T> {

    private String message;
    private Status status;
    private String devMessage;

    private T metaData;

    public MetaData() {
        this.status = Status.OK;
    }

    public MetaData(String message) {
        this.message = message;
        this.status = Status.OK;
    }

    public MetaData(Status status, String devMessage) {
        this.status = status;
        this.devMessage = devMessage;
    }

    public MetaData(String message, Status status, String devMessage) {
        this.message = message;
        this.status = status;
        this.devMessage = devMessage;
    }

    public MetaData(T metaData, String message) {
        this.message = message;
        this.status = Status.OK;
        this.metaData = metaData;
    }

    public MetaData(T metaData, Status status, String devMessage) {
        this.metaData = metaData;
        this.status = status;
        this.devMessage = devMessage;
    }

    public MetaData(T metaData, String message, Status status, String devMessage) {
        this.metaData = metaData;
        this.message = message;
        this.status = status;
        this.devMessage = devMessage;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }

    public void setMetaData(T metaData) {
        this.metaData = metaData;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public T getMetaData() {
        return metaData;
    }

    @Override
    public String toString() {
        return "MetaData{" + "message=" + message + ", status=" + status + ", devMessage=" + devMessage + ", metaData=" + metaData + '}';
    }

}
