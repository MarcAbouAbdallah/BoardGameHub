@Service
public class BorrowingService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    /**Create a borrow request */
    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDto dto) {
        Player requester = playerRepo.findById(dto.getRequesterId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Requester not found."));
        Player requestee = playerRepo.findById(dto.getRequesteeId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Requestee not found."));
        GameCopy gameCopy = gameCopyRepo.findById(dto.getGameCopyId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Game copy not found."));

        if (requester.equals(requestee)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "You cannot borrow a game from yourself.");
        }
        if (dto.getEndDate().before(dto.getStartDate())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "End date must be after start date.");
        }

        BorrowRequest borrowRequest = new BorrowRequest(requester, requestee, gameCopy, dto.getComment(), dto.getStartDate(), dto.getEndDate());
        return borrowRequestRepo.save(borrowRequest);
    }

    /**Get all borrow requests */
    public List<BorrowRequest> getAllBorrowRequests() {
        return (List<BorrowRequest>) borrowRequestRepo.findAll();
    }

    /** Get a specific borrow request */
    public BorrowRequest findBorrowRequestById(int requestId) {
        return borrowRequestRepo.findById(requestId)
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "No borrow request found with ID " + requestId));
    }

    /** Get all borrow requests sent by a given player */
    public List<BorrowRequest> getRequestsSentByPlayer(int playerId) {
        Player player = playerRepo.findById(playerId)
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Player not found."));
        return borrowRequestRepo.findByRequester(player);
    }

    /** Get all borrow requests received by a player */
    public List<BorrowRequest> getRequestsReceivedByPlayer(int playerId) {
        Player player = playerRepo.findById(playerId)
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Player not found."));
        return borrowRequestRepo.findByRequestee(player);
    }

    /** Approve || Reject a borrow request */
    @Transactional
    public BorrowRequest updateBorrowRequestStatus(int requestId, BorrowStatus newStatus) {
        BorrowRequest request = findBorrowRequestById(requestId);

        // Ensure only requestee can change the status
        Player requestee = request.getRequestee();
        if (requestee == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Requestee not found.");
        }
        if (newStatus != BorrowStatus.ACCEPTED && newStatus != BorrowStatus.DECLINED) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Invalid status update. Use ACCEPTED or DECLINED.");
        }
        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Only pending requests can be updated.");
        }

        request.setStatus(newStatus);
        return borrowRequestRepo.save(request);
    }

    /**Update a borrow request -Only requester can update */
    @Transactional
    public BorrowRequest updateBorrowRequest(@Valid BorrowRequestUpdateDto dto, int requestId, int requesterId) {
        BorrowRequest request = findBorrowRequestById(requestId);
        if (request.getRequester().getId() != requesterId) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requester can update this request.");
        }

        if (dto.getComment() != null) request.setComment(dto.getComment());
        if (dto.getStartDate() != null) request.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) {
            if (dto.getEndDate().before(request.getStartDate())) {
                throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "End date must be after start date.");
            }
            request.setEndDate(dto.getEndDate());
        }

        return borrowRequestRepo.save(request);
    }

    /** Deletes a borrow request - Only requester can delete) */
    @Transactional
    public void deleteBorrowRequest(int requestId) {
        BorrowRequest request = findBorrowRequestById(requestId);
        borrowRequestRepo.delete(request);
    }
}
