package com.k.smarthomer.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.k.smarthomer.Models.Devices.Device;
import com.k.smarthomer.Models.Home;
import com.k.smarthomer.Models.Room;

public class FirebaseDatabaseInstance {

    public static final String USERS = "users";
    public static final String HOMES = "homes";
    public static final String ROOMS = "rooms";

    private static final String DEVICE_IDS = "deviceIds";

    public static final String DEVICES = "devices";
    public static final String BOILERS = DEVICES + "/Boilers";
    public static final String FANS = DEVICES + "/Fans";
    public static final String SWITCHES = DEVICES + "/Switches";
    public static final String TEMPERATURES = DEVICES + "/Temperatures";
    public static final String AIR_CONDITIONERS = DEVICES + "/Air Conditioners";


    private static DatabaseReference database;

    public static DatabaseReference getRootDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    public static DatabaseReference getUsersDatabase() {
        return getRootDatabase().child(USERS);
    }

    public static DatabaseReference getDevicesDatabase() {
        return getRootDatabase().child(DEVICES);
    }

    public static DatabaseReference getHomesDatabase() {
        return getRootDatabase().child(HOMES);
    }

    public static DatabaseReference getRoomsDatabase() {
        return getRootDatabase().child(ROOMS);
    }

    public static DatabaseReference getSwitchesDatabase() {
        return getDevicesDatabase().child(SWITCHES);
    }

    public static DatabaseReference getFansDatabase() {
        return getDevicesDatabase().child(FANS);
    }

    public static DatabaseReference getAirConditionersDatabase() {
        return getDevicesDatabase().child(AIR_CONDITIONERS);
    }

    public static DatabaseReference getTemperaturesDatabase() {
        return getDevicesDatabase().child(TEMPERATURES);
    }

    public static DatabaseReference getBoilersDatabase() {
        return getDevicesDatabase().child(BOILERS);
    }

    public static DatabaseReference getUserById(String id) {
        return getUsersDatabase().child(id);
    }

    public static DatabaseReference getHomesWithUserId(String id) {
        return getUserById(id).child(HOMES);
    }

    public static DatabaseReference getHomeWithHomeId(String id) {
        return getHomesDatabase().child(id);
    }

    public static DatabaseReference getRoomsWithHomeId(String id) {
        return getHomesDatabase().child(id).child(ROOMS);
    }

    public static DatabaseReference getRoomWithId(String homeId, String roomId){
        return getHomeWithHomeId(homeId).child(ROOMS).child(roomId);
    }

    public static DatabaseReference getDeviceWithDeviceId(String id){
        return getDevicesDatabase().child(id);
    }

    public static DatabaseReference getDeviceIdsWithHomeId(String homeId, String roomId){
        return getHomeWithHomeId(homeId).child(ROOMS).child(roomId).child(DEVICE_IDS);
    }

    public static void createHome(Home home) {
        getHomesDatabase().child(home.getHomeId()).setValue(home);
    }

    public static void createRoom(Home home, Room room) {
        getRoomsWithHomeId(home.getHomeId()).child(room.getRoomId()).setValue(Room.class);
    }

    public static void createDevice(Device device) {
        getDevicesDatabase().child(device.getDeviceId()).setValue(device);
    }

}
