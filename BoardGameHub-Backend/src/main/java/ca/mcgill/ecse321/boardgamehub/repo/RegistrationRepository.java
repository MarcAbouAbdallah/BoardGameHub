package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.model.Player;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RegistrationRepository extends CrudRepository<Registration, Registration.Key> {
    public Registration findRegistrationByKey(Registration.Key key);
    public List<Registration> findRegistrationsByPlayer(Player registrant);
    public List<Registration> findRegistrationsByEvent(Event registeredEvent);
    public List<Registration> findAll();
    public int countByKey_RegisteredEvent(Event registeredEvent);
}
