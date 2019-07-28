package com.ulanm.moneytransfer.model.impl;

import com.ulanm.moneytransfer.model.GenericModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class User implements GenericModel {

    private final String id;

    private String name;

    private LocalDateTime creationDateTime;

    public User() {
        this(UUID.randomUUID().toString());
    }

    public User(String id) {
        this.id = id;
        creationDateTime = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) &&
                Objects.equals(this.name, user.name) &&
                Objects.equals(this.creationDateTime, user.creationDateTime);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss d-MMMM-yyyy");
        return "User {\n" +
                "\tID: " + id + ",\n" +
                "\tName: " + name + ",\n" +
                "\tCreated at: " + creationDateTime.format(formatter) + "\n" +
                "}\n";
    }

    @Override
    public User clone() {
        try {super.clone();}
        catch (CloneNotSupportedException ignored) {}
        User clone = new User(this.id);
        clone.name = this.name;
        clone.creationDateTime = this.creationDateTime;
        return clone;
    }

}
