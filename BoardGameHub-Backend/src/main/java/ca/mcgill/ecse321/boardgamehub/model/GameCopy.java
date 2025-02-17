package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class GameCopy {
    @Id
    @GeneratedValue
    private int id;

    private boolean isAvailable;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Player owner;

    protected GameCopy(){
    }

    public GameCopy(boolean isAvailable, Game game, Player owner) {
        this.isAvailable = isAvailable;
        this.game = game;
        this.owner = owner;
    }

    public int getId(){
        return id;
    }

    public boolean getIsAvailable(){
        return isAvailable;
    }

    public Game getGame(){
        return game;
    }

    public Player getOwner(){
        return owner;
    }

    public boolean setId(int aId){
        this.id = aId;
        return true;
    }

    public boolean setIsAvailable(boolean aIsAvailable){
        this.isAvailable = aIsAvailable;
        return true;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
            "id" + ":" + getId() + "," +
            "isAvailable" + ":" + getIsAvailable() + "," +
            "game" + ":" + (game != null ? game.getName() : "null") + "," +
            "owner" + ":" + (owner != null ? owner.getName() : "null") + "]";
    }
}
