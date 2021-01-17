package com.k.smarthomer.Models.Devices;

public class Boiler implements Device {
    private String deviceId, deviceName;
    private int connectionState;
    private boolean waterAvailable, state, heatedUp;
    private static final int deviceType = BOILER;
    private String devicePin;

    public Boiler() {

    }

    public Boiler(String deviceId, String deviceName, int connectionState, boolean waterAvailable, boolean state, boolean heatedUp, String devicePin) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.connectionState = connectionState;
        this.waterAvailable = waterAvailable;
        this.state = state;
        this.heatedUp = heatedUp;
        this.devicePin = devicePin;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public boolean isWaterAvailable() {
        return waterAvailable;
    }

    public void setWaterAvailable(boolean waterAvailable) {
        this.waterAvailable = waterAvailable;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isHeatedUp() {
        return heatedUp;
    }

    public void setHeatedUp(boolean heatedUp) {
        this.heatedUp = heatedUp;
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
