package devices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Device {

    @Expose
    @SerializedName("name")
    protected String name;

    @Expose
    @SerializedName("status")
    protected Status status;

    public Device(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Status {
        ON, OFF
    }

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
