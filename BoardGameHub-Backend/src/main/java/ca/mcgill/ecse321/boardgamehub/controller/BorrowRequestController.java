package ca.mcgill.ecse321.boardgamehub.controller;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestDto;
import ca.mcgill.ecse321.boardgamehub.service.BorrowingService;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrowRequests")
public class BorrowRequestController {

    private final BorrowingService borrowingService;

    @Autowired
    public BorrowRequestController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /** Creates a new borrow request */
    @PostMapping("/create")
    public ResponseEntity<BorrowRequestDto> createBorrowRequest(@RequestBody BorrowRequestCreationDto dto) {
        return new ResponseEntity<>(BorrowRequestDto.fromEntity(borrowingService.createBorrowRequest(dto)), HttpStatus.CREATED);
    }

    /** Retrieves all borrow requests */
    @GetMapping("/all")
    public ResponseEntity<List<BorrowRequestDto>> getAllBorrowRequests() {
        return ResponseEntity.ok(
            borrowingService.getAllBorrowRequests().stream()
                .map(BorrowRequestDto::fromEntity)
                .collect(Collectors.toList())
        );
    }

    /** Retrieve a specific borrow request by ID */
    @GetMapping("/{requestId}")
    public ResponseEntity<BorrowRequestDto> getBorrowRequestById(@PathVariable Integer requestId) {
        return ResponseEntity.ok(BorrowRequestDto.fromEntity(borrowingService.findBorrowRequestById(requestId)));
    }

    /** Retrieve all borrow requests sent by a player */
    @GetMapping("/sent/{playerId}")
    public ResponseEntity<List<BorrowRequestDto>> getRequestsSentByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.ok(
            borrowingService.getRequestsSentByPlayer(playerId).stream()
                .map(BorrowRequestDto::fromEntity)
                .collect(Collectors.toList())
        );
    }

    /** Retrieves borrow requests received by a player */
    @GetMapping("/received/{playerId}")
    public ResponseEntity<List<BorrowRequestDto>> getRequestsReceivedByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.ok(
            borrowingService.getRequestsReceivedByPlayer(playerId).stream()
                .map(BorrowRequestDto::fromEntity)
                .collect(Collectors.toList())
        );
    }

    /** Approves a borrow request */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<BorrowRequestDto> approveBorrowRequest(@PathVariable Integer requestId, @RequestParam Integer requesteeId) {
        return ResponseEntity.ok(BorrowRequestDto.fromEntity(borrowingService.approveBorrowRequest(requestId, requesteeId)));
    }

    /** Rejects a borrow request */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<BorrowRequestDto> rejectBorrowRequest(@PathVariable Integer requestId, @RequestParam Integer requesteeId) {
        return ResponseEntity.ok(BorrowRequestDto.fromEntity(borrowingService.rejectBorrowRequest(requestId, requesteeId)));
    }

    /** Updates a borrow request */
    @PutMapping("/{requestId}/update")
    public ResponseEntity<BorrowRequestDto> updateBorrowRequest(@PathVariable Integer requestId, @RequestParam Integer requesterId, @RequestBody BorrowRequestUpdateDto dto) {
        return ResponseEntity.ok(BorrowRequestDto.fromEntity(borrowingService.updateBorrowRequest(dto, requestId, requesterId)));
    }

    /** Deletes a borrow request */
    @DeleteMapping("/{requestId}/delete")
    public ResponseEntity<Void> deleteBorrowRequest(@PathVariable Integer requestId, @RequestParam Integer requesterId) {
        borrowingService.deleteBorrowRequest(requestId, requesterId);
        return ResponseEntity.ok().build();
    }
}
