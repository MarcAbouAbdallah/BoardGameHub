package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardgamehub.model.Player;

@SpringBootTest
public class PlayerRepositoryTests {
    @Autowired
    private PlayerRepository playerRepository;

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
}
