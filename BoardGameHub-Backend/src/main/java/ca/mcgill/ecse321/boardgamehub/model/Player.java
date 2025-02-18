package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Entity
public class Player {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String email;
    private String password;
    private boolean isGameOwner;

    protected Player() {
    }

    public Player(String name, String email, String password, boolean isGameOwner) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isGameOwner = isGameOwner;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsGameOwner() {
        return isGameOwner;
    }

    public boolean setId(int aId) {
        id = aId;
        return true;
    }

    public boolean setName(String aName) {
        name = aName;
        return true;
    }

    public boolean setEmail(String aEmail) {
        email = aEmail;
        return true;
    }

    public boolean setPassword(String aPassword) {
        password = aPassword;
        return true;
    }

    public boolean setIsGameOwner(boolean aIsGameOwner) {
        isGameOwner = aIsGameOwner;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
                "id" + ":" + getId() + "," +
                "name" + ":" + getName() + "," +
                "email" + ":" + getEmail() + "," +
                "owner" + ":" + getIsGameOwner() +
                "]";
    }
}
