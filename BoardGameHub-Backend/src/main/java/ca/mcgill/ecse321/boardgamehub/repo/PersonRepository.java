package ca.mcgill.ecse321.boardgamehub.repo;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.boardgamehub.model.Person;


public interface PersonRepository extends CrudRepository<Person, Integer> {
    public Person findPersonById(int id);
}
