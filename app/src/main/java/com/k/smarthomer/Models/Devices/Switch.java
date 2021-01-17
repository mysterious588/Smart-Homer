package com.k.smarthomer.Models.Devices;

public class Switch implements Device {

    private String deviceId, deviceName;
    private boolean state;
    private int connectionState, deviceType = SWITCH;
    private String devicePin;

    public Switch() {
    }

    public Switch(String deviceId, String deviceName, boolean state, int connectionState, String devicePin) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.state = state;
        this.connectionState = connectionState;
        this.devicePin = devicePin;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public void setDevicePin(String devicePin) {
        this.devicePin = devicePin;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getDevicePin() {
        return devicePin;
    }

    @Override
    public int getDeviceType() {
        return deviceType;
    }

    @Override
    public int getConnectionState() {
        return connectionState;
    }

    @Override
    public void onReceive() {

    }

    @Override
    public void send() {

    }

    @Override
    public boolean getState() {
        return state;
    }
}
