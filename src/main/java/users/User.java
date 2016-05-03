package users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import devices.Device;
import org.lightcouch.Document;

import java.util.HashSet;
import java.util.Set;

public class User extends Document {

    @Expose
    private
    String username;

    @Expose
    private String email;

    @Expose
    private String phone;

    @Expose
    private Set<Device> devices;

    private String token;

    private final static Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public User(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;

        devices = new HashSet<>();
    }

    public boolean addDevice(Device device) {
        return devices.add(device);
    }

    public boolean removeDevice(Device device) {
        return devices.remove(device);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
