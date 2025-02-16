package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private int maxPlayers;
    private int minPlayers;
    private String description;

    @OneToMany
    private List<GameCopy> copies;

    protected Game(){
    }

    public Game(String name, int maxPlayers, int minPlayers, String description) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this. minPlayers =  minPlayers;
        this.description = description;
    }

    //Getters

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

    public List<GameCopy> getCopies(){
        return copies;
    }

    //Setters

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

    public void setCopies(List<GameCopy> copies){
        this.copies = copies;
    }

    //toString
    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", minPlayers=" + minPlayers +
                ", description='" + description + '\'' +
                ", copies=" + (copies != null ? copies.size() : 0) + " copies" +
                '}';
    }

}
