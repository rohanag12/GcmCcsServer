package users;

import com.google.gson.annotations.Expose;
import devices.Device;

import java.util.HashSet;
import java.util.Set;

public class User {

    @Expose
    String username;

    @Expose
    String email;

    @Expose
    String phone;

    @Expose
    Set<Device> devices;

    String clientId;

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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
