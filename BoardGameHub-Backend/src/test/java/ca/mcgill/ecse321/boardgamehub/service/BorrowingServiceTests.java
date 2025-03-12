package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.sql.Date;
import java.util.Optional;

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
import org.springframework.http.HttpStatus;

public class BorrowingServiceTests {

    @Mock
    private BorrowRequestRepository mockBorrowRequestRepo;
    
    @Mock
    private PlayerRepository mockPlayerRepo;
    
    @Mock
    private GameCopyRepository mockGameCopyRepo;
    
    @InjectMocks
    private BorrowingService borrowingService;

    private static final int REQUESTER_ID = 1;
    private static final int REQUESTEE_ID = 2;
    private static final int GAME_COPY_ID = 3;
    private static final int BORROW_REQUEST_ID = 100;
    
    private static final String TEST_COMMENT = "Requesting to borrow.";
    private static final Date START_DATE = Date.valueOf("2025-02-15");
    private static final Date END_DATE = Date.valueOf("2025-02-20");
    
    private Player requester, requestee;
    private GameCopy gameCopy;
    private BorrowRequest borrowRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        requester = new Player("Alice", "alice@example.com", "password123", false);
        requester.setId(REQUESTER_ID);
        
        requestee = new Player("Bob", "bob@example.com", "securePass", true);
        requestee.setId(REQUESTEE_ID);

        gameCopy = new GameCopy();
        gameCopy.setId(GAME_COPY_ID);
        
        borrowRequest = new BorrowRequest(requester, requestee, gameCopy, TEST_COMMENT, START_DATE, END_DATE);
        borrowRequest.setId(BORROW_REQUEST_ID);
    }

    @Test
    public void testCreateBorrowRequest_Success() {
        BorrowRequestCreationDto dto = new BorrowRequestCreationDto(REQUESTER_ID, REQUESTEE_ID, GAME_COPY_ID, TEST_COMMENT, START_DATE, END_DATE);
        
        when(mockPlayerRepo.findById(REQUESTER_ID)).thenReturn(Optional.of(requester));
        when(mockPlayerRepo.findById(REQUESTEE_ID)).thenReturn(Optional.of(requestee));
        when(mockGameCopyRepo.findById(GAME_COPY_ID)).thenReturn(Optional.of(gameCopy));
        when(mockBorrowRequestRepo.save(any(BorrowRequest.class))).thenReturn(borrowRequest);

        BorrowRequest createdRequest = borrowingService.createBorrowRequest(dto);

        assertNotNull(createdRequest);
        assertEquals(TEST_COMMENT, createdRequest.getComment());
        verify(mockBorrowRequestRepo, times(1)).save(any(BorrowRequest.class));
    }
}
