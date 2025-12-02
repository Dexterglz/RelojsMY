package com.example.myhealthlife.model;

public class EventBusMessageEvent {
    // Estados posibles (defínelos según tu lógica)
    public static final int DISCONNECT = 0;
    public static final int CONNECTED = 1;
    public static final int TIMEOUT = 2;
    public static final int DISCONNECTING = 3;
    public static final int CONNECTING = 4;

    // Variable para almacenar el estado
    public int belState;
    public String deviceName; // ← nuevo campo

    // Constructor (opcional)
    public EventBusMessageEvent(int belState, String deviceName) {
        this.belState = belState;
        this.deviceName = deviceName;
    }

    public EventBusMessageEvent() {

    }

    // Métodos getter/setter (opcional)
    public int getBelState() {
        return belState;
    }

    public void setBelState(int belState) {
        this.belState = belState;
    }
}
