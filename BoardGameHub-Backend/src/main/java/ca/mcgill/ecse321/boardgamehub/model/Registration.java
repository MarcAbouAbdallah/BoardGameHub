package ca.mcgill.ecse321.boardgamehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Registration {
    @Id
    @GeneratedValue
    /* private int EventID;
    private int PlayerID; */
    private int id;
    @ManyToOne
    private Player registrant;
    @ManyToOne
    private Event registeredEvent;

    protected Registration() {
    }

    public Registration(Player registrant, Event registeredEvent) {
        this.registrant = registrant;
        this.registeredEvent = registeredEvent;
    }

    public int getId() {
        return id;
    }

    public Player getRegistrant() {
        return registrant;
    }

    public Event getRegisteredEvent() {
        return registeredEvent;
    }

    public void setId(int aId) {
        id = aId;
    }
    
    public void setRegistrant(Player aRegistrant) {
        registrant = aRegistrant;
    }

    public void setRegisteredEvent(Event aRegisteredEvent) {
        registeredEvent = aRegisteredEvent;
    }

    public String toString() {
        return super.toString() + "[" +
                "id" + ":" + getId() + "," +
                "registrant" + ":" + getRegistrant() + "," +
                "event" + ":" + getRegisteredEvent() + "," +
                "]";
    }
}


