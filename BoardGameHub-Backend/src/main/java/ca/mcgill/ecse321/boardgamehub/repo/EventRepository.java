package ca.mcgill.ecse321.boardgamehub.repo;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.boardgamehub.model.Event;

public interface EventRepository extends CrudRepository<Event, Integer> {
    public Event findEventById(int id);
}