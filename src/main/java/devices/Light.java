package devices;

public class Light extends Device {

    Light(String name, String room) {
        super(name, room);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Light light = (Light) o;

        return name != null && light.name != null && name.equals(light.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
