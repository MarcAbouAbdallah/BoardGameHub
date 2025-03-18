package ca.mcgill.ecse321.boardgamehub.integration;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BorrowRequestIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    private static final Player REQUESTER = new Player("Alice", "alice@mail.com", "password123", false);
    private static final Player REQUESTEE = new Player("Bob", "bob@mail.com", "securePass", true);
    private static final Game GAME = new Game("Catan", 4, 2, "Strategy game");
    private static final GameCopy GAME_COPY = new GameCopy(true, GAME, REQUESTEE);

    private static final String COMMENT = "Request to borrow this game.";
    private static final LocalDate START_DATE = LocalDate.now().plusDays(2);
    private static final LocalDate END_DATE = LocalDate.now().plusDays(7);

    private static int createdBorrowRequestId;

    @BeforeAll
    public void setup() {
        playerRepo.save(REQUESTER);
        playerRepo.save(REQUESTEE);
        gameRepo.save(GAME);
        gameCopyRepo.save(GAME_COPY);
    }

    @AfterAll
    public void clearDatabase() {
        borrowRequestRepo.deleteAll();
        gameCopyRepo.deleteAll();
        gameRepo.deleteAll();
        playerRepo.deleteAll();
    }

    //Test creating a valid borrow request
    @Test
    @Order(0)
    public void testCreateValidBorrowRequest() {
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(
                REQUESTER.getId(),
                REQUESTEE.getId(),
                GAME_COPY.getId(),
                COMMENT,
                START_DATE,
                END_DATE
        );

        ResponseEntity<BorrowRequestResponseDto> response = client.postForEntity(
                "/borrow-requests", dto, BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        BorrowRequestResponseDto createdRequest = response.getBody();
        assertNotNull(createdRequest);
        assertTrue(createdRequest.getId() > 0);
        assertEquals(REQUESTER.getId(), createdRequest.getRequesterId());
        assertEquals(REQUESTEE.getId(), createdRequest.getRequesteeId());
        assertEquals(GAME_COPY.getId(), createdRequest.getGameCopyId());
        assertEquals(COMMENT, createdRequest.getComment());
        assertEquals(BorrowStatus.PENDING, createdRequest.getStatus());

        createdBorrowRequestId = createdRequest.getId();
    }

    //Test creating a borrow request with an invalid game copy ID
    @Test
    @Order(1)
    public void testCreateBorrowRequest_InvalidGameCopy() {
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(
                REQUESTER.getId(),
                REQUESTEE.getId(),
                999, // Invalid game copy ID
                COMMENT,
                START_DATE,
                END_DATE
        );

        ResponseEntity<String> response = client.postForEntity(
                "/borrow-requests", dto, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Game copy not found"));
    }

    //  Test retrieving a borrow request by ID
    @Test
    @Order(2)
    public void testGetBorrowRequestById() {
        ResponseEntity<BorrowRequestResponseDto> response = client.getForEntity(
                "/borrow-requests/" + createdBorrowRequestId, BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto request = response.getBody();
        assertNotNull(request);
        assertEquals(createdBorrowRequestId, request.getId());
    }

    //Test retrieving a non-existent borrow request
    @Test
    @Order(3)
    public void testGetBorrowRequestById_NotFound() {
        int invalidId = createdBorrowRequestId + 100;
        ResponseEntity<String> response = client.getForEntity("/borrow-requests/" + invalidId, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("No borrow request found"));
    }

    //Test updating borrow request comment
    @Test
    @Order(4)
    public void testUpdateBorrowRequest() {
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto(
                "Updated comment",
                START_DATE.plusDays(1),
                END_DATE.plusDays(1)
        );

        String url = "/borrow-requests/" + createdBorrowRequestId + "?requesterId=" + REQUESTER.getId();

        ResponseEntity<BorrowRequestResponseDto> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(updateDto), BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto updatedRequest = response.getBody();
        assertNotNull(updatedRequest);
        assertEquals("Updated comment", updatedRequest.getComment());
    }

    //Test updating a borrow request with an unauthorized requester
    @Test
    @Order(5)
    public void testUpdateBorrowRequest_Unauthorized() {
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto(
                "Invalid update",
                START_DATE.plusDays(2),
                END_DATE.plusDays(2)
        );

        String url = "/borrow-requests/" + createdBorrowRequestId + "?requesterId=" + REQUESTEE.getId();

        ResponseEntity<String> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(updateDto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Only the requester can update this request"));
    }

    //Test deleting a borrow request
    @Test
    @Order(6)
    public void testDeleteBorrowRequest() {
        String url = "/borrow-requests/" + createdBorrowRequestId + "?requesterId=" + REQUESTER.getId();

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    //Test deleting a borrow request with an invalid requester
    @Test
    @Order(7)
    public void testDeleteBorrowRequest_Unauthorized() {
        String url = "/borrow-requests/" + createdBorrowRequestId + "?requesterId=" + REQUESTEE.getId();

        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Only the requester can delete this request"));
    }
}
