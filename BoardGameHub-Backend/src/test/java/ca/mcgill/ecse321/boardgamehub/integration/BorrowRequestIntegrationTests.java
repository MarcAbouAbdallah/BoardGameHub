package ca.mcgill.ecse321.boardgamehub.integration;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowStatusUpdateDto;
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
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BorrowRequestIntegrationTests {

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
    private static final Game GAME = new Game("Catan", 4, 2, "Strategy game", "https://images.unsplash.com/photo-1619163413327-546fdb903195?q=80&w=1287&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
    private static final GameCopy GAME_COPY = new GameCopy(true, GAME, REQUESTEE);

    private static final String COMMENT = "Request to borrow this game.";
    private static final LocalDate START_DATE = LocalDate.now().plusDays(5);
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


    @Test
    @Order(0)
    public void testCreateValidBorrowRequest() {
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(
                REQUESTER.getId(),
                REQUESTEE.getId(),
                GAME_COPY.getId(),
                COMMENT,
                START_DATE,
                END_DATE);

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
        assertEquals(START_DATE, createdRequest.getStartDate());
        assertEquals(END_DATE, createdRequest.getEndDate());

        createdBorrowRequestId = createdRequest.getId();
    }

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

        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Game copy not found"));
    }

    @Test
    @Order(2)
    public void testGetBorrowRequestByValidId() {
        ResponseEntity<BorrowRequestResponseDto> response = client.getForEntity(
                "/borrow-requests/" + createdBorrowRequestId, BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto borrow_request = response.getBody();
        assertNotNull(borrow_request);

        // Compare with the created borrow request (from test 0)
        assertEquals(createdBorrowRequestId, borrow_request.getId());
        assertEquals(REQUESTER.getId(), borrow_request.getRequesterId());
        assertEquals(REQUESTEE.getId(), borrow_request.getRequesteeId());
        assertEquals(GAME_COPY.getId(), borrow_request.getGameCopyId());
        assertEquals(COMMENT, borrow_request.getComment());
        assertEquals(START_DATE, borrow_request.getStartDate());
        assertEquals(END_DATE, borrow_request.getEndDate());
    }

    @Test
    @Order(3)
    public void testGetBorrowRequestById_NotFound() {
        int invalidId = createdBorrowRequestId + 100;
        ResponseEntity<String> response = client.getForEntity("/borrow-requests/" + invalidId, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("No borrow request found"));
    }

    @Test
    @Order(4)
    public void testGetAllBorrowRequests() {
        ResponseEntity<BorrowRequestResponseDto[]> response = client.getForEntity(
                "/borrow-requests", BorrowRequestResponseDto[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto[] requests = response.getBody();
        assertNotNull(requests);
        assertEquals(1, requests.length);

        BorrowRequestResponseDto borrow_request = requests[0];
        assertNotNull(borrow_request);

        // Compare with the created borrow request (from test 0)
        assertEquals(createdBorrowRequestId, borrow_request.getId());
        assertEquals(REQUESTER.getId(), borrow_request.getRequesterId());
        assertEquals(REQUESTEE.getId(), borrow_request.getRequesteeId());
        assertEquals(GAME_COPY.getId(), borrow_request.getGameCopyId());
        assertEquals(COMMENT, borrow_request.getComment());
        assertEquals(START_DATE, borrow_request.getStartDate());
        assertEquals(END_DATE, borrow_request.getEndDate());
    }

    @Test
    @Order(5)
    public void testGetRequestsbyRequester(){
        ResponseEntity<BorrowRequestResponseDto[]> response = client.getForEntity(
                "/borrow-requests/requester/" + REQUESTER.getId(), BorrowRequestResponseDto[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto[] requests = response.getBody();
        assertNotNull(requests);
        assertEquals(1, requests.length);

        BorrowRequestResponseDto borrow_request = requests[0];
        assertNotNull(borrow_request);

        // Compare with the created borrow request (from test 0)
        assertEquals(createdBorrowRequestId, borrow_request.getId());
        assertEquals(REQUESTER.getId(), borrow_request.getRequesterId());
        assertEquals(REQUESTEE.getId(), borrow_request.getRequesteeId());
        assertEquals(GAME_COPY.getId(), borrow_request.getGameCopyId());
        assertEquals(COMMENT, borrow_request.getComment());
        assertEquals(START_DATE, borrow_request.getStartDate());
        assertEquals(END_DATE, borrow_request.getEndDate());
    }

    @Test
    @Order(6)
    public void testGetRequestsbyRequestee(){
        ResponseEntity<BorrowRequestResponseDto[]> response = client.getForEntity(
                "/borrow-requests/requestee/" + REQUESTEE.getId(), BorrowRequestResponseDto[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto[] requests = response.getBody();
        assertNotNull(requests);
        assertEquals(1, requests.length);

        BorrowRequestResponseDto borrow_request = requests[0];
        assertNotNull(borrow_request);

        // Compare with the created borrow request (from test 0)
        assertEquals(createdBorrowRequestId, borrow_request.getId());
        assertEquals(REQUESTER.getId(), borrow_request.getRequesterId());
        assertEquals(REQUESTEE.getId(), borrow_request.getRequesteeId());
        assertEquals(GAME_COPY.getId(), borrow_request.getGameCopyId());
        assertEquals(COMMENT, borrow_request.getComment());
        assertEquals(START_DATE, borrow_request.getStartDate());
        assertEquals(END_DATE, borrow_request.getEndDate());
    }

    @Test
    @Order(7)
    public void testUpdateBorrowRequest() {
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto(
                "Updated comment",
                START_DATE.plusDays(1),
                END_DATE.plusDays(1)
        );

        String url = "/borrow-requests/" + createdBorrowRequestId + "?userId=" + REQUESTER.getId(); // The requester is updating the request

        ResponseEntity<BorrowRequestResponseDto> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(updateDto), BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto updatedRequest = response.getBody();
        assertNotNull(updatedRequest);
        assertEquals("Updated comment", updatedRequest.getComment());
        assertEquals(START_DATE.plusDays(1), updatedRequest.getStartDate());
        assertEquals(END_DATE.plusDays(1), updatedRequest.getEndDate());
    }

    @Test
    @Order(8)
    public void testUpdateBorrowRequest_Unauthorized() {
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto(
                "Invalid update",
                START_DATE.plusDays(2),
                END_DATE.plusDays(2)
        );

        // The user trying to update the request is not the requester (userId != REQUESTER.getId())
        String url = "/borrow-requests/" + createdBorrowRequestId + "?userId=" + REQUESTEE.getId();

        ResponseEntity<String> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(updateDto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("Only the requester can update this request"));
    }

    @Test
    @Order(9)
    public void testUpdateBorrowRequest_NotFound() {
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto(
                "Invalid update",
                START_DATE.plusDays(3),
                END_DATE.plusDays(3)
        );

        int invalidId = createdBorrowRequestId + 100;
        String url = "/borrow-requests/" + invalidId + "?userId=" + REQUESTER.getId();

        ResponseEntity<String> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(updateDto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("No borrow request found"));
    }

    @Test
    @Order(10)
    public void testUpdateBorrowRequestStatus_InvalidStatus() {
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.PENDING);

        String url = "/borrow-requests/" + createdBorrowRequestId + "/status";

        ResponseEntity<String> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(statusDto), String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("The request must either be accepted, declined or returned."));

    }

    @Test
    @Order(11)
    public void testAcceptBorrowRequest_Success() {
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.ACCEPTED);

        String url = "/borrow-requests/" + createdBorrowRequestId + "/status";

        ResponseEntity<BorrowRequestResponseDto> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(statusDto), BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto updatedRequest = response.getBody();
        assertNotNull(updatedRequest);
        assertEquals(BorrowStatus.ACCEPTED, updatedRequest.getStatus());
    }

    @Test
    @Order(12)
    public void testAcceptBorrowRequest_AlreadyAccepted() {
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.ACCEPTED);

        String url = "/borrow-requests/" + createdBorrowRequestId + "/status";
        

        ResponseEntity<String> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(statusDto), String.class);
        
        // The request is already accepted (from test 11)
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("Only pending requests can be accepted or declined."));
    }

    @Test
    @Order(13)
    public void testReturnBorrowRequest() {
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.RETURNED);

        String url = "/borrow-requests/" + createdBorrowRequestId + "/status";

        ResponseEntity<BorrowRequestResponseDto> response = client.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(statusDto), BorrowRequestResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        BorrowRequestResponseDto updatedRequest = response.getBody();
        assertNotNull(updatedRequest);
        assertEquals(BorrowStatus.RETURNED, updatedRequest.getStatus());
    }

    @Test
    @Order(14)
    public void testDeleteBorrowRequest_Unauthorized() {

        // The user trying to delete the request is not the requester (userId != REQUESTER.getId())
        String url = "/borrow-requests/" + createdBorrowRequestId + "?userId=" + REQUESTEE.getId();

        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        String error = response.getBody();
        assertNotNull(error);
        assertTrue(error.contains("Only the requester can delete this request."));
    }

    @Test
    @Order(15)
    public void testDeleteBorrowRequest() {
        String url = "/borrow-requests/" + createdBorrowRequestId + "?userId=" + REQUESTER.getId();

        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
