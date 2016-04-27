package devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Device {

    @Expose
    String name;

    @Expose
    Status status;

    static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    Device(String name) {
        this.name = name;
        switchOff();
    }

    public Status getStatus() {
        return status;
    }

    void switchOn() {
        // ToDo: Switch it ON !!
        status = Status.ON;
    }

    void switchOff() {
        // ToDo: Switch it OFF !!
        status = Status.OFF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
