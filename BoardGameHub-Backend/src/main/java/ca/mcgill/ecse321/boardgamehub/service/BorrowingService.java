package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.BorrowRequestUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.*;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class BorrowingService {

    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private GameCopyRepository gameCopyRepo;

    /**Creating a new borrow request **/
    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDto dto) {
        Player requester = playerRepo.findById(dto.getRequesterId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Requester not found."));
        Player requestee = playerRepo.findById(dto.getRequesteeId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Requestee not found."));
        GameCopy gameCopy = gameCopyRepo.findById(dto.getGameCopyId())
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "Game copy not found."));

        if (!gameCopy.getOwner().equals(requestee)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Requestee must be the owner of the game copy.");
        }
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

    /**Get a specific borrow request**/
    public BorrowRequest findBorrowRequestById(int requestId) {
        return borrowRequestRepo.findById(requestId)
            .orElseThrow(() -> new BoardGameHubException(HttpStatus.NOT_FOUND, "No borrow request found with ID " + requestId));
    }

    /** Approve or reject a borrow Request */
    @Transactional
    public BorrowRequest approveOrRejectBorrowRequest(int requestId, int requesteeId, BorrowStatus status) {
        BorrowRequest request = findBorrowRequestById(requestId);
        if (!request.getRequestee().getId().equals(requesteeId)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requestee can approve or reject this request.");
        }
        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Only pending requests can be modified.");
        }
        request.setStatus(status);
        return borrowRequestRepo.save(request);
    }

    /** Update a borrow request -Only requester can updat */
    @Transactional
    public BorrowRequest updateBorrowRequest(@Valid BorrowRequestUpdateDto dto, int requestId, int requesterId) {
        BorrowRequest request = findBorrowRequestById(requestId);
        if (!request.getRequester().getId().equals(requesterId)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requester can update this request.");
        }

        // Validate the update with the current request data
        dto.validate(request);

        if (dto.getComment() != null) request.setComment(dto.getComment());
        if (dto.getStartDate() != null) request.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) request.setEndDate(dto.getEndDate());

        return borrowRequestRepo.save(request);
    }

    /** Delete a borrow request- Only requester can delete */
    @Transactional
    public void deleteBorrowRequest(int requestId, int requesterId) {
        BorrowRequest request = findBorrowRequestById(requestId);
        if (!request.getRequester().getId().equals(requesterId)) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requester can delete this request.");
        }
        borrowRequestRepo.delete(request);
    }
}
