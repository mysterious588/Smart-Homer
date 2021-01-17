package com.k.smarthomer.Models;

import java.util.HashMap;

public class Home {
    private String homeId;
    private String homeName, pinNumber;
    private HashMap<String, Room> rooms;

    public Home() {

    }

    public Home(String homeId, String pinNumber, String homeName, HashMap<String, Room> rooms) {
        this.homeId = homeId;
        this.pinNumber = pinNumber;
        this.homeName = homeName;
        this.rooms = rooms;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public HashMap<String, Room> getRooms() {
        return rooms;
    }

    public void setRooms(HashMap<String, Room> rooms) {
        this.rooms = rooms;
    }
}
