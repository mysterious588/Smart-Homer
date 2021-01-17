package com.k.smarthomer.Models.Devices;

public class TemperatureSensor implements Device {

    private String deviceId, deviceName;
    private float temperature;
    private int connectionState, deviceType = TEMPERATURE_SENSOR;
    private boolean state;
    private String devicePin;

    public TemperatureSensor() {
    }

    public TemperatureSensor(String deviceId, String deviceName, float temperature, int connectionState, boolean state, String devicePin) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.temperature = temperature;
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

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
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

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean getState() {
        return false;
    }
}
