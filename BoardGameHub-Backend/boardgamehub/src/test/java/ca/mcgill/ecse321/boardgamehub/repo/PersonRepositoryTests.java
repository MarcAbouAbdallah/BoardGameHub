package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Person;

@SpringBootTest
public class PersonRepositoryTests {
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndReadPerson() {
        //Arrange
        String name = "John";
        String email = "john@gmail.com";
        String password = "John@123";
        Person John = new Person(name, email, password);
        John = personRepository.save(John);

        //Act
        Person JohnFromDb = personRepository.findPersonById(John.getId());
        

        //Assert
        assertNotNull(JohnFromDb);
        assertEquals(John.getName(), JohnFromDb.getName());
        assertEquals(John.getEmail(), JohnFromDb.getEmail());
        assertEquals(John.getPassword(), JohnFromDb.getPassword());
        assertEquals(John.getId(), JohnFromDb.getId());

    }
}
