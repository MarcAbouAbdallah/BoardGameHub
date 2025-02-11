package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

//Temporary class to prevent errors
@Entity
public class GameCopy {
    @Id
    @GeneratedValue
    private int id;

    public GameCopy() {
    }

    public int getId() {
        return id;
    }
}
