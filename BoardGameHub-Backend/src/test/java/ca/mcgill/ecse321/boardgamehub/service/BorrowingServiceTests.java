package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.sql.Date;
import java.util.Optional;
import java.util.List;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.*;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
public class BorrowingServiceTests {

    @Mock
    private BorrowRequestRepository mockBorrowRequestRepo;
    
    @Mock
    private PlayerRepository mockPlayerRepo;
    
    @Mock
    private GameCopyRepository mockGameCopyRepo;
    
    @InjectMocks
    private BorrowingService borrowingService;

    private static final int VALID_REQUESTER_ID = 1;
    private static final int VALID_REQUESTEE_ID = 2;
    private static final int VALID_GAME_COPY_ID = 3;
    private static final int VALID_BORROW_REQUEST_ID = 100;
    
    private static final String TEST_COMMENT = "Request to borrow this game.";
    private static final Date START_DATE = Date.valueOf("2025-02-15");
    private static final Date END_DATE = Date.valueOf("2025-02-20");
    
    private Player requester;
    private Player requestee;
    private GameCopy gameCopy;
    private BorrowRequest borrowRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        requester = new Player("Alice", "alice@example.com", "password123", false);
        requester.setId(VALID_REQUESTER_ID);
        
        requestee = new Player("Bob", "bob@example.com", "securePass", true);
        requestee.setId(VALID_REQUESTEE_ID);

        gameCopy = new GameCopy();
        gameCopy.setId(VALID_GAME_COPY_ID);
        gameCopy.setOwner(requestee);
        
        borrowRequest = new BorrowRequest(requester, requestee, gameCopy, TEST_COMMENT, START_DATE, END_DATE);
        borrowRequest.setId(VALID_BORROW_REQUEST_ID);
        borrowRequest.setStatus(BorrowStatus.PENDING);
    }

  
    @Test
    public void testCreateBorrowRequest_Success() {
        when(mockPlayerRepo.findById(VALID_REQUESTER_ID)).thenReturn(Optional.of(requester));
        when(mockPlayerRepo.findById(VALID_REQUESTEE_ID)).thenReturn(Optional.of(requestee));
        when(mockGameCopyRepo.findById(VALID_GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        when(mockBorrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(borrowRequest);

        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, TEST_COMMENT, START_DATE, END_DATE);
        BorrowRequest createdRequest = borrowingService.createBorrowRequest(dto);

        assertNotNull(createdRequest);
        assertEquals(BorrowStatus.PENDING, createdRequest.getStatus());
        verify(mockBorrowRequestRepo, times(1)).save(any(BorrowRequest.class));
    }

    @Test
    public void testApproveBorrowRequest_Success() {
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        borrowRequest.setStatus(BorrowStatus.ACCEPTED);

        BorrowRequest approvedRequest = borrowingService.updateBorrowRequestStatus(VALID_BORROW_REQUEST_ID, VALID_REQUESTEE_ID, BorrowStatus.ACCEPTED);
        assertEquals(BorrowStatus.ACCEPTED, approvedRequest.getStatus());
        verify(mockBorrowRequestRepo, times(1)).save(borrowRequest);
    }

    @Test
    public void testRejectBorrowRequest_Success() {
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        borrowRequest.setStatus(BorrowStatus.DECLINED);

        BorrowRequest rejectedRequest = borrowingService.updateBorrowRequestStatus(VALID_BORROW_REQUEST_ID, VALID_REQUESTEE_ID, BorrowStatus.DECLINED);
        assertEquals(BorrowStatus.DECLINED, rejectedRequest.getStatus());
        verify(mockBorrowRequestRepo, times(1)).save(borrowRequest);
    }

    @Test
    public void testUpdateBorrowRequest_Success() {
        BorrowRequestUpdateDto dto = new BorrowRequestUpdateDto("Updated comment", START_DATE, END_DATE);
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        borrowRequest.setComment(dto.getComment());
        
        BorrowRequest updatedRequest = borrowingService.updateBorrowRequest(dto, VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID);
        assertEquals("Updated comment", updatedRequest.getComment());
        verify(mockBorrowRequestRepo, times(1)).save(borrowRequest);
    }

    @Test
    public void testDeleteBorrowRequest_Success() {
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        assertDoesNotThrow(() -> borrowingService.deleteBorrowRequest(VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID));
        verify(mockBorrowRequestRepo, times(1)).delete(borrowRequest);
    }

    @Test
    public void testFindBorrowRequestById_Success() {
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        BorrowRequest foundRequest = borrowingService.findBorrowRequestById(VALID_BORROW_REQUEST_ID);
        assertNotNull(foundRequest);
        assertEquals(VALID_BORROW_REQUEST_ID, foundRequest.getId());
    }

    @Test
    public void testFindAllBorrowRequests_Success() {
        when(mockBorrowRequestRepo.findAll()).thenReturn(List.of(borrowRequest));
        List<BorrowRequest> requests = borrowingService.getAllBorrowRequests();
        assertEquals(1, requests.size());
        assertEquals(VALID_BORROW_REQUEST_ID, requests.get(0).getId());
    }

    @Test
    public void testApproveAlreadyApprovedRequest_Fails() {
        borrowRequest.setStatus(BorrowStatus.ACCEPTED);
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));

        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> 
            borrowingService.updateBorrowRequestStatus(VALID_BORROW_REQUEST_ID, VALID_REQUESTEE_ID, BorrowStatus.ACCEPTED));

        assertEquals("Only pending requests can be approved.", exception.getMessage());
    }
     @Test
    public void testUpdateBorrowRequest_FailsIfNotRequester() {
        BorrowRequestUpdateDto dto = new BorrowRequestUpdateDto("Updated comment", START_DATE, END_DATE);
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> 
            borrowingService.updateBorrowRequest(dto, VALID_REQUESTEE_ID, VALID_BORROW_REQUEST_ID));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    public void testDeleteBorrowRequest_FailsIfNotRequester() {
        when(mockBorrowRequestRepo.findById(VALID_BORROW_REQUEST_ID)).thenReturn(Optional.of(borrowRequest));
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> 
            borrowingService.deleteBorrowRequest(VALID_BORROW_REQUEST_ID, VALID_REQUESTEE_ID));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    public void testGetAllBorrowRequests_EmptyList() {
        when(mockBorrowRequestRepo.findAll()).thenReturn(List.of());
        List<BorrowRequest> requests = borrowingService.getAllBorrowRequests();
        assertTrue(requests.isEmpty());
    }
}
