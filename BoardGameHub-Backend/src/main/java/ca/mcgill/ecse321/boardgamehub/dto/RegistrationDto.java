package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.Registration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationDto {
    @NotNull(message = "Registrant ID cannot be empty")
    private int registrantId;
    @NotBlank(message = "Registrant name cannot be empty")
    private String registrant;
    @NotNull(message = "RegisteredEvent ID cannot be empty")
    private int registeredEventId;
    @NotBlank(message = "RegisteredEvent name cannot be empty")
    private String registeredEvent;

    public RegistrationDto() {}

    public RegistrationDto(Registration registration) {
        this.registrantId = registration.getKey().getRegistrant().getId();
        this.registrant = registration.getKey().getRegistrant().getName();
        this.registeredEventId = registration.getKey().getRegisteredEvent().getId();
        this.registeredEvent = registration.getKey().getRegisteredEvent().getName();
    }
    
    public int getRegistrantId() {
        return registrantId;
    }

    public int getRegisteredEventId() {
        return registeredEventId;
    }
    
    public String getRegistrant() {
        return registrant;
    }

    public String getRegisteredEvent() {
        return registeredEvent;
    }
    

    public void setRegistrantId(int registrantId) {
        this.registrantId = registrantId;
    }

    public void setRegisteredEventId(int registeredEventId) {
        this.registeredEventId = registeredEventId;
    }

    public void setRegistrant(String registrant) {
        this.registrant = registrant;
    }

    public void setRegisteredEvent(String registeredEvent) {
        this.registeredEvent = registeredEvent;
    }
}
