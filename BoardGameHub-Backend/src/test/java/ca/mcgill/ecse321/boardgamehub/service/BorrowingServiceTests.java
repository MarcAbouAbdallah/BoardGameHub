package ca.mcgill.ecse321.boardgamehub.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowStatusUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.*;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;


@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BorrowingServiceTests {
    @Mock
    private BorrowRequestRepository mockBorrowRequestRepo;
    @Mock
    private PlayerRepository mockPlayerRepo;
    @Mock
    private GameRepository mockGameRepo;
    @Mock
    private GameCopyRepository mockGameCopyRepo;
    @InjectMocks
    private BorrowingService borrowingService;

    private static final int VALID_REQUESTER_ID = 1;
    private static final int VALID_REQUESTEE_ID = 2;
    private static final int VALID_GAME_COPY_ID = 4;
    private static final int VALID_BORROW_REQUEST_ID = 100;
    
    private static final String COMMENT = "Request to borrow this game.";
    private static final LocalDate START_DATE = LocalDate.of(2026, 2, 15);
    private static final LocalDate END_DATE = LocalDate.of(2026, 2, 18);
    
    private static final Player REQUESTER = new Player("Alice", "alice@example.com", "password123", false);
    private static final Player REQUESTEE = new Player("Bob", "bob@example.com", "securePass", true);
    private static final Game GAME = new Game("Monopoly", 4, 2, "A game", "https://images.unsplash.com/photo-1640461470346-c8b56497850a?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
    private static final GameCopy VALID_GAME = new GameCopy(GAME, REQUESTEE);
    
    @Test
    public void testCreateValidBorrowRequest(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        when(mockGameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(VALID_GAME);
        when(mockBorrowRequestRepo.save(any(BorrowRequest.class))).thenAnswer((InvocationOnMock invocation) -> {
            BorrowRequest request = invocation.getArgument(0);
            request.setId(VALID_BORROW_REQUEST_ID);
            return request;
        });
        
        BorrowRequest request = null;
        try {
            request = borrowingService.createBorrowRequest(requestToCreate);
        } catch (BoardGameHubException e) {
            fail();
        }

        assertNotNull(request);
        assertEquals(VALID_BORROW_REQUEST_ID, request.getId());
        assertEquals(REQUESTER, request.getRequester());
        assertEquals(REQUESTEE, request.getRequestee());
        assertEquals(VALID_GAME, request.getGame());
        assertEquals(COMMENT, request.getComment());
        assertEquals(Date.valueOf(START_DATE), request.getStartDate());
        assertEquals(Date.valueOf(END_DATE), request.getEndDate());
    }

    @Test
    public void testCreateRequestWithInvalidRequester(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no requester with ID " + VALID_REQUESTER_ID + ".", exception.getMessage());
    }

    @Test
    public void testCreateRequestWithInvalidRequestee(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no requestee with ID " + VALID_REQUESTEE_ID + ".", exception.getMessage());
    }

    @Test
    public void testCreateRequestWithInvalidGameCopy(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        when(mockGameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game copy not found.", exception.getMessage());
    }

    @Test
    public void testCreateRequestWithRequesteeNotOwner(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);

        // Game copy is not owned by the requestee (request must be sent to the owner of the game)
        GameCopy invalidGameCopy = new GameCopy(GAME, REQUESTER);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        when(mockGameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(invalidGameCopy);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Requestee must be the owner of the game.", exception.getMessage());
    }

    @Test
    public void testCreateInvalidRequesttoSelf(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTEE_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, START_DATE, END_DATE);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        when(mockGameCopyRepo.findGameCopyById(VALID_GAME_COPY_ID)).thenReturn(VALID_GAME);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("You cannot borrow a game from yourself.", exception.getMessage());
    }

    @Test
    public void testCreateRequestWithInvalidDates(){
        BorrowRequestCreationDto requestToCreate = new BorrowRequestCreationDto(VALID_REQUESTER_ID, VALID_REQUESTEE_ID, VALID_GAME_COPY_ID, COMMENT, END_DATE, START_DATE);
        
        // Start date is after end date
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.createBorrowRequest(requestToCreate);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("End date must be after start date.", exception.getMessage());
    }

    @Test
    public void testFindValidBorrowRequestById(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request.setId(VALID_BORROW_REQUEST_ID);
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BorrowRequest foundRequest = borrowingService.findBorrowRequestById(VALID_BORROW_REQUEST_ID);

        assertNotNull(foundRequest);
        assertEquals(VALID_BORROW_REQUEST_ID, foundRequest.getId());
        assertEquals(REQUESTER, foundRequest.getRequester());
        assertEquals(REQUESTEE, foundRequest.getRequestee());
        assertEquals(VALID_GAME, foundRequest.getGame());
        assertEquals(COMMENT, foundRequest.getComment());
        assertEquals(Date.valueOf(START_DATE), foundRequest.getStartDate());
        assertEquals(Date.valueOf(END_DATE), foundRequest.getEndDate());
    }

    @Test
    public void testFindInvalidBorrowRequestById(){
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.findBorrowRequestById(VALID_BORROW_REQUEST_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No borrow request found with ID " + VALID_BORROW_REQUEST_ID, exception.getMessage());
    }
    
    @Test
    public void testApproveValidBorrowRequest(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));

        request.setId(VALID_BORROW_REQUEST_ID);
        request.setStatus(BorrowStatus.PENDING);

        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        when(mockBorrowRequestRepo.save(request)).thenReturn(request);
        
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.ACCEPTED);
        BorrowRequest approvedRequest = borrowingService.updateRequestStatus(VALID_BORROW_REQUEST_ID, statusDto);
        
        assertNotNull(approvedRequest);
        assertEquals(BorrowStatus.ACCEPTED, approvedRequest.getStatus());
    }

    @Test
    public void testRejectValidBorrowRequest(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));

        request.setId(VALID_BORROW_REQUEST_ID);
        request.setStatus(BorrowStatus.PENDING);

        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        when(mockBorrowRequestRepo.save(request)).thenReturn(request);
        
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.DECLINED);
        BorrowRequest rejectedRequest = borrowingService.updateRequestStatus(VALID_BORROW_REQUEST_ID, statusDto);
        
        assertNotNull(rejectedRequest);
        assertEquals(BorrowStatus.DECLINED, rejectedRequest.getStatus());
    }

    @Test
    public void testApproveAlreadyApprovedRequest(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));

        request.setId(VALID_BORROW_REQUEST_ID);
        request.setStatus(BorrowStatus.ACCEPTED); // The request is already accepted

        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.DECLINED);
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.updateRequestStatus(VALID_BORROW_REQUEST_ID, statusDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Only pending requests can be accepted or declined.", exception.getMessage());
    }

    @Test
    public void testApproveRequestWithInvalidStatus(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));

        request.setId(VALID_BORROW_REQUEST_ID);
        request.setStatus(BorrowStatus.PENDING);

        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.PENDING);
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.updateRequestStatus(VALID_BORROW_REQUEST_ID, statusDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("The request must either be accepted, declined or returned.", exception.getMessage());
    }

    // @Test
    // public void testReturnBorrowRequest(){
    //     BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));

    //     request.setId(VALID_BORROW_REQUEST_ID);
    //     request.setStatus(BorrowStatus.ACCEPTED);

    //     when(mockBorrowRequestRepo.save(request)).thenReturn(request);
    //     when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
    //     BorrowStatusUpdateDto statusDto = new BorrowStatusUpdateDto(BorrowStatus.RETURNED);
    //     BorrowRequest returnedRequest = borrowingService.updateRequestStatus(VALID_BORROW_REQUEST_ID, statusDto);
        
    //     assertNotNull(returnedRequest);
    //     assertEquals(BorrowStatus.RETURNED, returnedRequest.getStatus());
    // }

    @Test
    public void testUpdateValidBorrowRequest(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        REQUESTER.setId(VALID_REQUESTER_ID);
    
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto("Updated comment", START_DATE, END_DATE);
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        when(mockBorrowRequestRepo.save(request)).thenReturn(request);
        
        BorrowRequest updatedRequest = borrowingService.updateBorrowRequest(updateDto, VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID);
        
        assertNotNull(updatedRequest);
        assertEquals("Updated comment", updatedRequest.getComment());
        // Dates are unchanged in the request (null in updateDTO)
        assertEquals(Date.valueOf(START_DATE), updatedRequest.getStartDate());
        assertEquals(Date.valueOf(END_DATE), updatedRequest.getEndDate());
    }

    @Test
    public void testUpdateRequestWithInvalidRequester(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        REQUESTER.setId(VALID_REQUESTER_ID + 1); // Invalid requester ID
    
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto("Updated comment", START_DATE, END_DATE);
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.updateBorrowRequest(updateDto, VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Only the requester can update this request.", exception.getMessage());
    }

    @Test
    public void testUpdateRequestWithInvalidDates(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        REQUESTER.setId(VALID_REQUESTER_ID);
    
        BorrowRequestUpdateDto updateDto = new BorrowRequestUpdateDto("Updated comment", END_DATE, START_DATE);
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.updateBorrowRequest(updateDto, VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("New start date must be before the new end date.", exception.getMessage());
    }

    @Test
    public void testDeleteValidBorrowRequest(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        REQUESTER.setId(VALID_REQUESTER_ID);
    
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        assertDoesNotThrow(() -> borrowingService.deleteBorrowRequest(VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID));
        verify(mockBorrowRequestRepo, times(1)).delete(request);
    }

    @Test
    public void testDeleteRequestWithInvalidRequester(){
        BorrowRequest request = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        REQUESTER.setId(VALID_REQUESTER_ID + 1); // Invalid requester ID
    
        when(mockBorrowRequestRepo.findBorrowRequestById(VALID_BORROW_REQUEST_ID)).thenReturn(request);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.deleteBorrowRequest(VALID_BORROW_REQUEST_ID, VALID_REQUESTER_ID);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Only the requester can delete this request.", exception.getMessage());

        // The Request is not deleted
        verify(mockBorrowRequestRepo, never()).delete(request);
    }

    @Test
    public void testGetAllBorrowRequests(){
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        when(mockBorrowRequestRepo.findAll()).thenReturn(requests);
        
        List<BorrowRequest> allRequests = borrowingService.getAllBorrowRequests();
        
        assertEquals(3, allRequests.size());
    }

    @Test
    public void testGetAllBorrowRequests_EmptyList() {
        when(mockBorrowRequestRepo.findAll()).thenReturn(List.of());
        List<BorrowRequest> requests = borrowingService.getAllBorrowRequests();
        assertTrue(requests.isEmpty());
    }

    @Test
    public void testGetAllRequestsByRequester(){
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        
        List<BorrowRequest> allRequests = borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, null);
        
        assertEquals(3, allRequests.size());
    }

    /*
     * The error thrown used in this test is no longer used
     */

    // @Test
    // public void testGetAllRequestsByRequester_EmptyList() {
    //     when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
    //     when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(List.of());
        
    //     BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
    //         borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, null);
    //     });

    //     assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    //     assertEquals("No borrow requests found for requester with ID "+ VALID_REQUESTER_ID, exception.getMessage());
    // }

    @Test
    public void testGetAcceptedRequestsByRequester(){
        BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request1.setStatus(BorrowStatus.ACCEPTED);
        BorrowRequest request2 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request2.setStatus(BorrowStatus.ACCEPTED);
        BorrowRequest request3 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request3.setStatus(BorrowStatus.PENDING);
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
        when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        
        List<BorrowRequest> allRequests = borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, "ACCEPTED");
        
        assertEquals(2, allRequests.size());
    }

    /*
     * The error thrown expected in this test is no longer thrown
     */
    // @Test
    // public void testGetAcceptedRequestsByRequester_EmptyList() {
    //     when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
    //     when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(List.of());
        
    //     BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
    //         borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, "ACCEPTED");
    //     });

    //     assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    //     assertEquals("No borrow requests found for requester with ID "+ VALID_REQUESTER_ID, exception.getMessage());
    // }

    // @Test
    // public void testGetReturnedRequestsByRequester(){
    //     BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
    //     request1.setStatus(BorrowStatus.RETURNED);
    //     BorrowRequest request2 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
    //     request2.setStatus(BorrowStatus.RETURNED);
    //     BorrowRequest request3 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
    //     request3.setStatus(BorrowStatus.PENDING);
    //     List<BorrowRequest> requests = new ArrayList<>();
    //     requests.add(request1);
    //     requests.add(request2);
    //     requests.add(request3);
    //     when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(requests);
    //     when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        
    //     List<BorrowRequest> allRequests = borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, "RETURNED");
        
    //     assertEquals(2, allRequests.size());
    // }

    @Test
    public void testGetRequestsByRequester_InvalidFilter(){
        BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request1.setStatus(BorrowStatus.PENDING);
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(request1);
        when(mockBorrowRequestRepo.findByRequester(REQUESTER)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(REQUESTER);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, "INVALID");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid status filter. Must be one of: PENDING, ACCEPTED, DECLINED, RETURNED.", exception.getMessage());
    }

    @Test
    public void testGetAllRequestsByRequestee(){
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        requests.add(new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE)));
        when(mockBorrowRequestRepo.findByRequestee(REQUESTEE)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        
        List<BorrowRequest> allRequests = borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, null);
        
        assertEquals(3, allRequests.size());
    }

    /*
     * The error thrown expected in this error is no longer thrown
     */
    // @Test
    // public void testGetAllRequestsByRequestee_EmptyList() {
    //     when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
    //     when(mockBorrowRequestRepo.findByRequestee(REQUESTEE)).thenReturn(List.of());
        
    //     BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
    //         borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, null);
    //     });

    //     assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    //     assertEquals("No borrow requests found for requestee with ID "+ VALID_REQUESTEE_ID, exception.getMessage());
    // }

    @Test
    public void testGetAcceptedRequestsByRequestee(){
        BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request1.setStatus(BorrowStatus.ACCEPTED);
        BorrowRequest request2 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request2.setStatus(BorrowStatus.ACCEPTED);
        BorrowRequest request3 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request3.setStatus(BorrowStatus.PENDING);
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
        when(mockBorrowRequestRepo.findByRequestee(REQUESTEE)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        
        List<BorrowRequest> allRequests = borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, "ACCEPTED");
        
        assertEquals(2, allRequests.size());
    }

    @Test
    public void testGetLendingHistoryofRequestee(){
        BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request1.setStatus(BorrowStatus.ACCEPTED);
        BorrowRequest request2 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request2.setStatus(BorrowStatus.PENDING);
        BorrowRequest request3 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request3.setStatus(BorrowStatus.PENDING);
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
        when(mockBorrowRequestRepo.findByRequestee(REQUESTEE)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        
        List<BorrowRequest> allRequests = borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, "HISTORY");
        
        assertEquals(1, allRequests.size());
    }

    @Test
    public void testGetRequestsByRequestee_InvalidFilter(){
        BorrowRequest request1 = new BorrowRequest(REQUESTER, REQUESTEE, VALID_GAME, COMMENT, Date.valueOf(START_DATE), Date.valueOf(END_DATE));
        request1.setStatus(BorrowStatus.ACCEPTED);
        List<BorrowRequest> requests = new ArrayList<>();
        requests.add(request1);
        when(mockBorrowRequestRepo.findByRequestee(REQUESTEE)).thenReturn(requests);
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(REQUESTEE);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, "INVALID");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid status filter. Must be one of: PENDING, ACCEPTED, DECLINED, RETURNED, HISTORY.", exception.getMessage());
    }

    @Test
    public void testGetRequestsByRequester_RequesterNotFound(){
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTER_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.getRequestsByRequester(VALID_REQUESTER_ID, null);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no requester with ID " + VALID_REQUESTER_ID + ".", exception.getMessage());
        verify(mockBorrowRequestRepo, never()).findByRequester(any(Player.class));
    }

    @Test
    public void testGetRequestsByRequestee_RequesteeNotFound(){
        when(mockPlayerRepo.findPlayerById(VALID_REQUESTEE_ID)).thenReturn(null);
        
        BoardGameHubException exception = assertThrows(BoardGameHubException.class, () -> {
            borrowingService.getRequestsByRequestee(VALID_REQUESTEE_ID, null);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no requestee with ID " + VALID_REQUESTEE_ID + ".", exception.getMessage());
        verify(mockBorrowRequestRepo, never()).findByRequestee(any(Player.class));
    }
}

