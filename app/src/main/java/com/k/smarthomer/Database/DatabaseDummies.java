package com.k.smarthomer.Database;

import com.k.smarthomer.Models.Devices.AirConditioner;
import com.k.smarthomer.Models.Devices.Boiler;
import com.k.smarthomer.Models.Devices.Device;
import com.k.smarthomer.Models.Devices.Fan;
import com.k.smarthomer.Models.Devices.Switch;
import com.k.smarthomer.Models.Devices.TemperatureSensor;

public class DatabaseDummies {
    private static void createDummyDevice(String randomId, int type) {
        switch (type) {
            case Device.AIR_CONDITIONER:
                AirConditioner airConditioner = new AirConditioner(randomId, "Air conditioner", false, 0, 25,17, 30, "fff2");
                FirebaseDatabaseInstance.createDevice(airConditioner);
                break;
            case Device.BOILER:
                Boiler boiler = new Boiler(randomId, "boiler", 0, true, true, false, "sss");
                FirebaseDatabaseInstance.createDevice(boiler);
                break;
            case Device.FAN:
                Fan fan = new Fan(randomId, "fan", 100, 0, true, "fff");
                FirebaseDatabaseInstance.createDevice(fan);
                break;
            case Device.SWITCH:
                Switch dev = new Switch(randomId, "Cable", true, 0, "ccc");
                FirebaseDatabaseInstance.createDevice(dev);
                break;
            case Device.TEMPERATURE_SENSOR:
                TemperatureSensor temperatureSensor = new TemperatureSensor(randomId, "temperature sensor", 25, 0, true, "ttt");
                FirebaseDatabaseInstance.createDevice(temperatureSensor);
                break;
        }
    }

    public static void createDevices(){
        createDummyDevice("air1", Device.AIR_CONDITIONER);
        createDummyDevice("boiler1", Device.BOILER);
        createDummyDevice("fan1", Device.FAN);
        createDummyDevice("switch1", Device.SWITCH);
        createDummyDevice("temp1", Device.TEMPERATURE_SENSOR);
    }
}
