@RestController
@RequestMapping("/borrowRequests")
public class BorrowRequestController {

    private final BorrowingService borrowingService;

    @Autowired
    public BorrowRequestController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /** Create a new borrow request (POST /borrowRequests) */
    @PostMapping
    public ResponseEntity<BorrowRequestResponse> createBorrowRequest(@RequestBody @Valid BorrowRequestCreationDto dto) {
        BorrowRequest borrowRequest = borrowingService.createBorrowRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(BorrowRequestResponse.fromBorrowRequest(borrowRequest));
    }

    /**Get all borrow requests (GET /borrowRequests) */
    @GetMapping
    public ResponseEntity<List<BorrowRequestResponse>> getAllBorrowRequests() {
        List<BorrowRequest> requests = borrowingService.getAllBorrowRequests();
        return ResponseEntity.ok(requests.stream().map(BorrowRequestResponse::fromBorrowRequest).toList());
    }

    /** get a specific borrow request (GET /borrowRequests/{requestId}) */
    @GetMapping("/{requestId}")
    public ResponseEntity<BorrowRequestResponse> getBorrowRequestById(@PathVariable Integer requestId) {
        BorrowRequest borrowRequest = borrowingService.findBorrowRequestById(requestId);
        return ResponseEntity.ok(BorrowRequestResponse.fromBorrowRequest(borrowRequest));
    }

    /**Update borrow request status */
    @PutMapping("/{requestId}/status")
    public ResponseEntity<BorrowRequestResponse> updateBorrowRequestStatus(
            @PathVariable Integer requestId, @RequestParam BorrowStatus status) {
        BorrowRequest updatedRequest = borrowingService.updateBorrowRequestStatus(requestId, status);
        return ResponseEntity.ok(BorrowRequestResponse.fromBorrowRequest(updatedRequest));
    }

    /** Delete a borrow request (DELETE /borrowRequests/{requestId}) */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteBorrowRequest(@PathVariable Integer requestId) {
        borrowingService.deleteBorrowRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
