package org.kafka.origin.vo;

public class DemoUser {
    private int id;
    private String name;

    public DemoUser(int id) {
        this.id = id;
    }

    public DemoUser(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DemoUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
