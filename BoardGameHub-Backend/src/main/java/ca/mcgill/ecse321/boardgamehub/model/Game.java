package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private int maxPlayers;
    private int minPlayers;
    private String description;

    protected Game(){
    }

    public Game(String name, int maxPlayers, int minPlayers, String description) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this. minPlayers =  minPlayers;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public int getMinPlayers(){
        return minPlayers;
    }

    public String getDescription(){
        return description;
    }

    public boolean setId(int aId) {
        this.id = aId;
        return true;
    }

    public boolean setName(String aName){
        this.name = aName;
        return true;
    }

    public boolean setMaxPlayers(int aMaxPlayers){
        this.maxPlayers = aMaxPlayers;
        return true;
    }

    public boolean setMinPlayers(int aMinPlayers){
        this.minPlayers = aMinPlayers;
        return true;
    }

    public boolean setDescription(String aDescription){
        this.description = aDescription;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
        "id" + ":" + getId() + "," +
        "name" + ":" + getName() + "," +
        "maxPlayers" + ":" + getMaxPlayers() + "," +
        "minPlayers" + ":" + getMinPlayers() + "," +
        "description" + ":" + getDescription() + "," +
        "]";
    }

}
