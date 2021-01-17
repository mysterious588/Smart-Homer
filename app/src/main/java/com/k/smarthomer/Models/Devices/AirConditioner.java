package com.k.smarthomer.Models.Devices;

public class AirConditioner implements Device {

    private String deviceId, deviceName;
    private boolean state;
    private int connectionState, temperature, deviceType = AIR_CONDITIONER, minTemp, maxTemp;
    private String devicePin;


    public AirConditioner() {
    }

    public AirConditioner(String deviceId, String deviceName, boolean state, int connectionState, int temperature, int minTemp, int maxTemp, String devicePin) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.state = state;
        this.connectionState = connectionState;
        this.temperature = temperature;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.devicePin = devicePin;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
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

    public void setDevicePin(String devicePin) {
        this.devicePin = devicePin;
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
