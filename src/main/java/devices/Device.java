package devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.lightcouch.Document;

public class Device extends Document {

    @Expose
    String name;

    @Expose
    String room;

    @Expose
    Status status;

    static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    Device(String name, String room) {
        this.name = name;
        this.room = room;
        switchOff();
    }

    public Status getStatus() {
        return status;
    }

    public void switchOn() {
        // ToDo: Switch it ON !!
        status = Status.ON;
    }

    public void switchOff() {
        // ToDo: Switch it OFF !!
        status = Status.OFF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    enum Status {
        ON, OFF
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return name != null && device.name != null && name.equals(device.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
