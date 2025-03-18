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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRequestResponseDto createBorrowRequest(@RequestBody BorrowRequestCreationDto dto) {
        BorrowRequest createdRequest = borrowingService.createBorrowRequest(dto);
        return new BorrowRequestResponseDto(createdRequest);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto getBorrowRequestById(@PathVariable int requestId) {
        BorrowRequest request = borrowingService.findBorrowRequestById(requestId);
        return new BorrowRequestResponseDto(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getAllBorrowRequests() {
        return borrowingService.getAllBorrowRequests().stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto updateBorrowRequest(@PathVariable int requestId,
                                                        @RequestParam int userId,
                                                        @RequestBody BorrowRequestUpdateDto dto) {
        BorrowRequest updatedRequest = borrowingService.updateBorrowRequest(dto, requestId, userId);
        return new BorrowRequestResponseDto(updatedRequest);
    }

    @PutMapping("/{requestId}/status")
    @ResponseStatus(HttpStatus.OK)
    public BorrowRequestResponseDto updateBorrowRequestStatus(@PathVariable int requestId,
                                                              @RequestParam int userId,
                                                              @RequestBody BorrowStatusUpdateDto status) {
        BorrowRequest updatedRequest = borrowingService.approveOrRejectBorrowRequest(requestId, userId, status);
        return new BorrowRequestResponseDto(updatedRequest);
    }

    @DeleteMapping("/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBorrowRequest(@PathVariable int requestId, @RequestParam int userId) {
        borrowingService.deleteBorrowRequest(requestId, userId);
    }

    @GetMapping("/requester/{requesterId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getRequestsByRequester(@PathVariable int requesterId) {
        return borrowingService.getRequestsByRequester(requesterId).stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/requestee/{requesteeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BorrowRequestResponseDto> getRequestsByRequestee(@PathVariable int requesteeId) {
        return borrowingService.getRequestsByRequestee(requesteeId).stream()
                .map(BorrowRequestResponseDto::new)
                .collect(Collectors.toList());
    }
}
