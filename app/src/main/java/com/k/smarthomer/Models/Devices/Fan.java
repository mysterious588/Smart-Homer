package com.k.smarthomer.Models.Devices;

public class Fan implements Device {

    private String deviceId, deviceName;
    private int power, connectionState;
    private boolean state;
    private int deviceType = FAN;
    private String devicePin;

    public Fan() {
    }

    public Fan(String deviceId, String deviceName, int power, int connectionState, boolean state, String devicePin) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.power = power;
        this.connectionState = connectionState;
        this.state = state;
        this.devicePin = devicePin;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public void setDevicePin(String devicePin) {
        this.devicePin = devicePin;
    }

    public void setState(boolean state) {
        this.state = state;
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
