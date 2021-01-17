package com.k.smarthomer.Models;

import java.util.ArrayList;

public class Room {
    private String roomId;
    private String roomName;

    private ArrayList<String> deviceIds;

    public Room() {

    }

    public Room(String roomId, String roomName, ArrayList<String> deviceIds) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.deviceIds = deviceIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public ArrayList<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(ArrayList<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
