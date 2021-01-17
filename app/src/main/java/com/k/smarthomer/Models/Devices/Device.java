package com.k.smarthomer.Models.Devices;

public interface Device {

    int CONNECTION_OK = 0, CONNECTION_BUSY = 1, CONNECTION_UNAVAILABLE = 2;
    int FAN = 0, AIR_CONDITIONER = 1, SWITCH = 2, TEMPERATURE_SENSOR = 3, BOILER = 4;
    int ON = 0, OFF = 1;

    String getDeviceName();

    String getDeviceId();

    String getDevicePin();

    int getDeviceType();

    int getConnectionState();

    void onReceive();

    void send();

    boolean getState();

    static String stateToString(int state) {
        switch (state) {
            case CONNECTION_OK:
                return "OK";
            case CONNECTION_BUSY:
                return "Busy";
            case CONNECTION_UNAVAILABLE:
                return "Unavailable";
        }
        return "empty";
    }
}
