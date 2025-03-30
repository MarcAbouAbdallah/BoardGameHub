package ca.mcgill.ecse321.boardgamehub.controller;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowStatusUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/borrow-requests")
public class BorrowRequestController {

    @Autowired
    private BorrowingService borrowingService;

    /**
     * Create a borrow request
     * 
     * @param dto an BorrowRequestCreationDto containing the borrow request to be created
     * @return A BorrowRequestResponseDto containing the created request with its id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDto createBorrowRequest(@RequestBody BorrowRequestCreationDto dto) {
        BorrowRequest createdRequest = borrowingService.createBorrowRequest(dto);
        return new BorrowRequestResponseDto(createdRequest);
    }

    /**
     * Get a particular request by its id
     * 
     * @param requestId the id of the request to get
     * @return A BorrowRequestResponseDto containing the request with the given id
     */
    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto getBorrowRequestById(@PathVariable int requestId) {
        BorrowRequest request = borrowingService.findBorrowRequestById(requestId);
        return new BorrowRequestResponseDto(request);
    }

    /**
     * Get all borrow requests
     * 
     * @return A List of BorrowRequestResponseDto containing all borrow requests
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getAllBorrowRequests() {
        return borrowingService.getAllBorrowRequests().stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all borrow requests sent by a player
     * 
     * @param requesterId The id of the requester
     * @param status Optional status filter (pending, accepted, declined, returned)
     * @return A List of BorrowRequestResponseDto containing all borrow requests sent by the player
     */
    @GetMapping("/requester/{requesterId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getRequestsByRequester(
            @PathVariable int requesterId,
            @RequestParam(required = false) String status) {

        return borrowingService.getRequestsByRequester(requesterId, status).stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all borrow requests received by a player
     * 
     * @param requesteeId The id of the requestee
     * @param status Optional status filter (pending, accepted, declined, returned, history)
     * @return A List of BorrowRequestResponseDto containing all borrow requests received by the player
     */
    @GetMapping("/requestee/{requesteeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getRequestsByRequestee
        (@PathVariable int requesteeId,
         @RequestParam(required = false) String status) {
        return borrowingService.getRequestsByRequestee(requesteeId, status).stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Update a borrow request
     * 
     * @param requestId The id of the request to update
     * @param dto A BorrowRequestUpdateDto containing the updates (comment, start date, end date)
     * @param userId The id of the user making the update (to check if they are the requester)
     * @return A BorrowRequestResponseDto containing the request with the given id
     */
    @PutMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto updateBorrowRequest(@PathVariable int requestId,
                                                        @RequestParam int userId,
                                                        @RequestBody BorrowRequestUpdateDto dto) {
        BorrowRequest updatedRequest = borrowingService.updateBorrowRequest(dto, requestId, userId);
        return new BorrowRequestResponseDto(updatedRequest);
    }

    /**
     * Approve or decling a borrow request
     * 
     * @param requestId The id of the request
     * @param status A BorrowStatusUpdateDto containing the updated status (accepted or declined)
     * @param userId The id of the user making the update (to check if they are the requestee i.e. the game owner)
     * @return A BorrowRequestResponseDto containing the request with the given id
     */
    @PutMapping("/{requestId}/status")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto updateBorrowRequestStatus(@PathVariable int requestId,
                                                              @RequestParam int userId,
                                                              @RequestBody BorrowStatusUpdateDto status) {
        BorrowRequest updatedRequest = borrowingService.approveOrRejectBorrowRequest(requestId, userId, status);
        return new BorrowRequestResponseDto(updatedRequest);
    }

    /**
     * Delete a borrow request
     * 
     * @param requestId The id of the request to delete
     * @param userId the id of the user making the delete request (to check if they are the requester)
     * @return void
     */
    @DeleteMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBorrowRequest(@PathVariable int requestId, @RequestParam int userId) {
        borrowingService.deleteBorrowRequest(requestId, userId);
    }
}
