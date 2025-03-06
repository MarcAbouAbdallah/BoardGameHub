package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.model.Event;

import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository extends CrudRepository<Registration, Registration.Key> {
    public Registration findRegistrationByKey(Registration.Key key);
    long countByKey_RegisteredEvent(Event registeredEvent);
}
