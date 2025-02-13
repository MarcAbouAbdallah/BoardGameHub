package ca.mcgill.ecse321.boardgamehub.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Registration {
    @EmbeddedId
    private Key key;

    @SuppressWarnings("unused")
    private Registration() {
    }

    public Registration(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
    
    @Embeddable
    public static class Key implements Serializable {
        @ManyToOne
        private Player registrant;
        @ManyToOne
        private Event registeredEvent;
        
        public Key() {
        }

        public Key(Player registrant, Event registeredEvent) {
            this.registrant = registrant;
            this.registeredEvent = registeredEvent;
        }

        public Player getRegistrant() {
            return registrant;
        }

        public Event getRegisteredEvent() {
            return registeredEvent;
        }

        public void setRegistrant(Player aRegistrant) {
            registrant = aRegistrant;
        }

        public void setRegisteredEvent(Event aRegisteredEvent) {
            registeredEvent = aRegisteredEvent;
        }

        public String toString() {
            return super.toString() + "[" +
                    "registrant" + ":" + getRegistrant() + "," +
                    "event" + ":" + getRegisteredEvent() + "," +
                    "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key other = (Key) obj;
            return this.getRegistrant().getId() == other.getRegistrant().getId()
                    && this.getRegisteredEvent().getId() == other.getRegisteredEvent().getId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getRegistrant().getId(), this.getRegisteredEvent().getId());
        }
    }
}

