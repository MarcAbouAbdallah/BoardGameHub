package ca.mcgill.ecse321.boardgamehub.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import ca.mcgill.ecse321.boardgamehub.model.*;

@SpringBootTest
public class BorrowRequestRepositoryTest {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameCopyRepository gameCopyRepository;

    @Autowired
    private GameRepository gameRepository;

    private Player requester;
    private Player requestee;
    private Game game;
    private GameCopy gameCopy;
    private BorrowRequest borrowRequest;

    @BeforeEach
    public void setup() {
        requester = new Player("Alice", "alice@example.com", "password123", false);
        playerRepository.save(requester);

        requestee = new Player("Bob", "bob@example.com", "securePass", true);
        playerRepository.save(requestee);

        game = new Game("Chess", 2, 2, "A strategic board game", "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        gameRepository.save(game);

        gameCopy = new GameCopy(true, game, requestee);
        gameCopyRepository.save(gameCopy);
    }

    @AfterEach
    public void cleanup() {
        borrowRequestRepository.deleteAll();
        gameCopyRepository.deleteAll();
        playerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadBorrowRequest() {
        // Arrange
        Date startDate = Date.valueOf("2025-02-15");
        Date endDate = Date.valueOf("2025-02-20");
        String comment = "I want to borrow this game";

        borrowRequest = new BorrowRequest(
            requester, requestee, gameCopy, 
            comment, 
            startDate, 
            endDate
        );
        borrowRequestRepository.save(borrowRequest);

        //Act 
        BorrowRequest foundRequest = borrowRequestRepository.findBorrowRequestById(borrowRequest.getId());

        //Assert
        assertNotNull(foundRequest);

        assertEquals(borrowRequest.getId(), foundRequest.getId());
        assertEquals(comment, foundRequest.getComment());
        assertEquals(startDate, foundRequest.getStartDate());
        assertEquals(endDate, foundRequest.getEndDate());
        assertEquals(BorrowStatus.PENDING, foundRequest.getStatus());

        assertEquals(requester.getId(), foundRequest.getRequester().getId());
        assertEquals(requestee.getId(), foundRequest.getRequestee().getId());
        assertEquals(gameCopy.getId(), foundRequest.getGame().getId());
    }

    @Test
    public void testDeleteBorrowRequest() {
        // Arrange
        Date startDate = Date.valueOf("2025-02-15");
        Date endDate = Date.valueOf("2025-02-20");
        String comment = "I want to borrow this game";

        borrowRequest = new BorrowRequest(
            requester, requestee, gameCopy, 
            comment, 
            startDate, 
            endDate
        );
        borrowRequestRepository.save(borrowRequest);

        //Act
        borrowRequestRepository.delete(borrowRequest);

        //Assert
        BorrowRequest foundRequest = borrowRequestRepository.findBorrowRequestById(borrowRequest.getId());
        assertNull(foundRequest);
    }

    @Test
    public void testUpdateBorrowRequest() {
        Date startDate = Date.valueOf("2025-02-15");
        Date endDate = Date.valueOf("2025-02-20");
        String comment = "I want to borrow this game";

        borrowRequest = new BorrowRequest(
            requester, requestee, gameCopy, 
            comment, 
            startDate, 
            endDate
        );
        borrowRequestRepository.save(borrowRequest);

        borrowRequest.setStatus(BorrowStatus.ACCEPTED);
        borrowRequestRepository.save(borrowRequest);

        BorrowRequest foundRequest = borrowRequestRepository.findBorrowRequestById(borrowRequest.getId());

        assertNotNull(foundRequest);
        assertEquals(BorrowStatus.ACCEPTED, foundRequest.getStatus()); // Updated borrow status
    }

    @Test
    public void testFindBorrowRequestByRequestee() {
        Date startDate = Date.valueOf("2025-02-15");
        Date endDate = Date.valueOf("2025-02-20");
        String comment = "I want to borrow this game";

        borrowRequest = new BorrowRequest(
            requester, requestee, gameCopy, 
            comment, 
            startDate, 
            endDate
        );
        borrowRequestRepository.save(borrowRequest);

        List<BorrowRequest> requestee_list = borrowRequestRepository.findByRequestee(requestee);

        assertFalse(requestee_list.isEmpty());
        assertEquals(1, requestee_list.size());

        BorrowRequest foundRequest = requestee_list.get(0);

        assertNotNull(foundRequest);
        assertEquals(borrowRequest.getId(), foundRequest.getId());
        assertEquals(borrowRequest.getRequestee().getId(), foundRequest.getRequestee().getId());
    }

    @Test
    public void testFindBorrowRequestByRequester() {
        Date startDate = Date.valueOf("2025-02-15");
        Date endDate = Date.valueOf("2025-02-20");
        String comment = "I want to borrow this game";

        borrowRequest = new BorrowRequest(
            requester, requestee, gameCopy, 
            comment, 
            startDate, 
            endDate
        );
        borrowRequestRepository.save(borrowRequest);

        List<BorrowRequest> requester_list = borrowRequestRepository.findByRequester(requester);

        assertFalse(requester_list.isEmpty());
        assertEquals(1, requester_list.size());

        BorrowRequest foundRequest = requester_list.get(0);

        assertNotNull(foundRequest);
        assertEquals(borrowRequest.getId(), foundRequest.getId());
        assertEquals(borrowRequest.getRequester().getId(), foundRequest.getRequester().getId());
    }
}
