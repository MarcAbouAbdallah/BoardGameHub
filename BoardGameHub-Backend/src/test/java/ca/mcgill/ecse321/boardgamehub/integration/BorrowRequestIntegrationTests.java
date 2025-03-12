package ca.mcgill.ecse321.boardgamehub.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.boardgamehub.dto.ErrorDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.model.*;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BorrowRequestIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    private int createdBorrowRequestId;

    private static final Player VALID_REQUESTER = new Player("Alice", "alice@example.com", "password123", false);
    private static final Player VALID_REQUESTEE = new Player("Bob", "bob@example.com", "securePass", true);
    private static final GameCopy VALID_GAME_COPY = new GameCopy();

    private static final String TEST_COMMENT = "Request to borrow this game.";
    private static final Date START_DATE = Date.valueOf("2025-02-15");
    private static final Date END_DATE = Date.valueOf("2025-02-20");

    @BeforeAll
    public void setup() {
        playerRepo.save(VALID_REQUESTER);
        playerRepo.save(VALID_REQUESTEE);
        gameCopyRepo.save(VALID_GAME_COPY);
    }

    @AfterAll
    public void clearDatabase() {
        borrowRequestRepo.deleteAll();
        playerRepo.deleteAll();
        gameCopyRepo.deleteAll();
    }

    /** Test Case 1: Successfully create a borrow request **/
    @Test
    @Order(1)
    public void testCreateValidBorrowRequest() {
        // Arrange
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(
                VALID_REQUESTER.getId(), VALID_REQUESTEE.getId(), VALID_GAME_COPY.getId(), TEST_COMMENT, START_DATE, END_DATE);

        // Act
        ResponseEntity<BorrowRequestDto> response = client.postForEntity("/borrowRequests/create", dto, BorrowRequestDto.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getId() >= 0, "The ID should be a positive integer");

        this.createdBorrowRequestId = response.getBody().getId();
        assertEquals(dto.getComment(), response.getBody().getComment());
        assertEquals(dto.getRequesterId(), response.getBody().getRequesterId());
        assertEquals(dto.getRequesteeId(), response.getBody().getRequesteeId());
        assertEquals(dto.getGameCopyId(), response.getBody().getGameCopyId());
    }

    /** Test Case 2: Cannot create borrow request with invalid requester **/
    @Test
    @Order(2)
    public void testCreateBorrowRequestFails_InvalidRequester() {
        // Arrange
        int invalidId = VALID_REQUESTER.getId() + 100;
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(invalidId, VALID_REQUESTEE.getId(), VALID_GAME_COPY.getId(), TEST_COMMENT, START_DATE, END_DATE);

        // Act
        ResponseEntity<ErrorDto> response = client.postForEntity("/borrowRequests/create", dto, ErrorDto.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Requester not found.", response.getBody().getErrors().get(0));
    }

    /** Test Case 3: Retrieve borrow request by ID **/
    @Test
    @Order(3)
    public void testGetValidBorrowRequestById() {
        // Act
        ResponseEntity<BorrowRequestDto> response = client.getForEntity("/borrowRequests/" + createdBorrowRequestId, BorrowRequestDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_COMMENT, response.getBody().getComment());
        assertEquals(VALID_REQUESTER.getId(), response.getBody().getRequesterId());
        assertEquals(VALID_REQUESTEE.getId(), response.getBody().getRequesteeId());
    }

    /**  Test Case 4: Approve a borrow request **/
    @Test
    @Order(4)
    public void testApproveBorrowRequest() {
        // Act
        ResponseEntity<BorrowRequestDto> response = client.exchange("/borrowRequests/" + createdBorrowRequestId + "/approve?requesteeId=" + VALID_REQUESTEE.getId(),
                org.springframework.http.HttpMethod.PUT, null, BorrowRequestDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BorrowStatus.ACCEPTED.toString(), response.getBody().getStatus());
    }

    /**  Test Case 5: Reject a borrow request **/
    @Test
    @Order(5)
    public void testRejectBorrowRequest() {
        // Act
        ResponseEntity<BorrowRequestDto> response = client.exchange("/borrowRequests/" + createdBorrowRequestId + "/reject?requesteeId=" + VALID_REQUESTEE.getId(),
                org.springframework.http.HttpMethod.PUT, null, BorrowRequestDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(BorrowStatus.DECLINED.toString(), response.getBody().getStatus());
    }

    /** Test Case 6: Update a borrow request **/
    @Test
    @Order(6)
    public void testUpdateBorrowRequest() {
        // Arrange
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto("Updated comment", null, null);

        // Act
        ResponseEntity<BorrowRequestDto> response = client.exchange("/borrowRequests/" + createdBorrowRequestId + "/update?requesterId=" + VALID_REQUESTER.getId(),
                org.springframework.http.HttpMethod.PUT, null, BorrowRequestDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated comment", response.getBody().getComment());
    }

    /** Test Case 7: Cannot approve non-existent borrow request **/
    @Test
    @Order(7)
    public void testApproveNonExistentBorrowRequest() {
        int invalidId = createdBorrowRequestId + 100;
        ResponseEntity<ErrorDto> response = client.exchange("/borrowRequests/" + invalidId + "/approve?requesteeId=" + VALID_REQUESTEE.getId(),
                org.springframework.http.HttpMethod.PUT, null, ErrorDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No borrow request found with ID " + invalidId, response.getBody().getErrors().get(0));
    }

    /** Test Case 8: Delete a borrow request **/
    @Test
    @Order(8)
    public void testDeleteBorrowRequest() {
        // Act
        ResponseEntity<Void> response = client.exchange("/borrowRequests/" + createdBorrowRequestId + "/delete?requesterId=" + VALID_REQUESTER.getId(),
                org.springframework.http.HttpMethod.DELETE, null, Void.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
