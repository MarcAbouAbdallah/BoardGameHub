package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;

@SpringBootTest
public class PlayerRepositoryTests {
    @Autowired
    private PlayerRepository playerRepository;


    @AfterEach
    public void clearDatabase() {
        playerRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadPlayer() {
        //Arrange
        String name = "John";
        String email = "john@gmail.com";
        String password = "John@123";
        boolean isGameOwner = true;
        Player John = new Player(name, email, password, isGameOwner);
        John = playerRepository.save(John);

        //Act
        Player JohnFromDb = playerRepository.findPlayerById(John.getId());
        

        //Assert
        assertNotNull(JohnFromDb);
        assertEquals(John.getName(), JohnFromDb.getName());
        assertEquals(John.getEmail(), JohnFromDb.getEmail());
        assertEquals(John.getPassword(), JohnFromDb.getPassword());
        assertEquals(John.getId(), JohnFromDb.getId());
        assertEquals(John.getIsGameOwner(), JohnFromDb.getIsGameOwner());

    }

    @Test
    public void testUpdatePlayer() {
        //Arrange
        String name = "John";
        String email = "john@gmail.com";
        String password = "John@123";
        boolean isGameOwner = true;
        Player John = new Player(name, email, password, isGameOwner);
        John = playerRepository.save(John);

        //Act
        John.setName("John Doe");
        John.setEmail("john.doe@gmail.com");
        John.setPassword("JohnDoe@123");
        John.setIsGameOwner(false);
        John = playerRepository.save(John);

        //Assert
        Player JohnFromDb = playerRepository.findPlayerById(John.getId());
        assertNotNull(JohnFromDb);
        assertEquals(John.getName(), JohnFromDb.getName());
        assertEquals(John.getEmail(), JohnFromDb.getEmail());
        assertEquals(John.getPassword(), JohnFromDb.getPassword());
        assertEquals(John.getId(), JohnFromDb.getId());
        assertEquals(John.getIsGameOwner(), JohnFromDb.getIsGameOwner());

    }

    @Test
    public void testDeletePlayer() {
        //Arrange
        String name = "John";
        String email = "john@gmail.com";
        String password = "John@123";
        boolean isGameOwner = true;
        Player John = new Player(name, email, password, isGameOwner);
        John = playerRepository.save(John);

        //Act
        playerRepository.delete(John);

        //Assert
        Player JohnFromDb = playerRepository.findPlayerById(John.getId());
        assertNull(JohnFromDb);
    }

    @Test
    public void testFindPlayerByEmail() {
        //Arrange
        String name = "John";
        String email = "john@gmail.com";
        String password = "John@123";
        boolean isGameOwner = true;
        Player John = new Player(name, email, password, isGameOwner);
        John = playerRepository.save(John);

        //Act
        Player JohnFromDb = playerRepository.findPlayerByEmail(email);

        //Assert
        assertNotNull(JohnFromDb);
        assertEquals(John.getName(), JohnFromDb.getName());
        assertEquals(John.getEmail(), JohnFromDb.getEmail());
        assertEquals(John.getPassword(), JohnFromDb.getPassword());
        assertEquals(John.getId(), JohnFromDb.getId());
        assertEquals(John.getIsGameOwner(), JohnFromDb.getIsGameOwner());
    }

    @Test
    public void testNonExistentPlayerByEmail() {
        //Arrange
        String email = "john.doe@gmail.com";

        //Act
        Player JohnFromDb = playerRepository.findPlayerByEmail(email);

        //Assert
        assertNull(JohnFromDb);
    }

    @Test
    public void testNonExistentPlayerById() {
        //Arrange
        int id = 1;

        //Act
        Player JohnFromDb = playerRepository.findPlayerById(id);

        //Assert
        assertNull(JohnFromDb);
    }

        

    
}
