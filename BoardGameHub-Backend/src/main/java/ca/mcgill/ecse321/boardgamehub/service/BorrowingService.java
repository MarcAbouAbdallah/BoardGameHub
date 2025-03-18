package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;
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

    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDto dto) {
        validateBorrowRequestCreationDto(dto);

        // Check if requester, requestee, and game copy exist
        Player requester = playerRepo.findPlayerById(dto.getRequesterId());
        if (requester == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("There is no requester with ID %d.",dto.getRequesterId()));
        }
        Player requestee = playerRepo.findPlayerById(dto.getRequesteeId());
        if (requestee == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("There is no requestee with ID %d.",dto.getRequesteeId()));
        }
        GameCopy gameCopy = gameCopyRepo.findGameCopyById(dto.getGameCopyId());
        if (gameCopy == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game copy not found.");
        }

        if (!gameCopy.getOwner().equals(requestee)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Requestee must be the owner of the game.");
        }
        if (requester.equals(requestee)) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "You cannot borrow a game from yourself.");
        }
        Date startDate = Date.valueOf(dto.getStartDate());
        Date endDate = Date.valueOf(dto.getEndDate());

        BorrowRequest borrowRequest = new BorrowRequest(requester, requestee, gameCopy, dto.getComment(), startDate, endDate);
        return borrowRequestRepo.save(borrowRequest);
    }

    public List<BorrowRequest> getAllBorrowRequests() {
        return (List<BorrowRequest>) borrowRequestRepo.findAll();
    }

    public BorrowRequest findBorrowRequestById(int requestId) {
        BorrowRequest request = borrowRequestRepo.findBorrowRequestById(requestId);
        if (request == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "No borrow request found with ID " + requestId);
        }
        return request;
    }

    @Transactional
    public BorrowRequest approveOrRejectBorrowRequest(int requestId, int requesteeId, BorrowStatus status) {
        BorrowRequest request = findBorrowRequestById(requestId);
        if (request.getRequestee().getId() != requesteeId) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "You are not allowed to approve or reject this request.");
        }
        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Only pending requests can be modified.");
        }
        if (status != BorrowStatus.ACCEPTED && status != BorrowStatus.DECLINED) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "The request must either be accepted or declined");
        }
        request.setStatus(status);
        return borrowRequestRepo.save(request);
    }

    @Transactional
    public BorrowRequest updateBorrowRequest(@Valid BorrowRequestUpdateDto updateDto, int requestId, int requesterId) {
        BorrowRequest request = findBorrowRequestById(requestId);

        // Only the requester can udpate the request's content
        if (request.getRequester().getId() != requesterId) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requester can update this request.");
        }

        // Validate the updateDto (non-null fields) with respect to current request
        updateDto.validate(request);

        if (updateDto.getComment() != null) request.setComment(updateDto.getComment());
        if (updateDto.getStartDate() != null) request.setStartDate(Date.valueOf(updateDto.getStartDate()));
        if (updateDto.getEndDate() != null) request.setEndDate(Date.valueOf(updateDto.getEndDate()));

        return borrowRequestRepo.save(request);
    }

    @Transactional
    public void deleteBorrowRequest(int requestId, int requesterId) {
        BorrowRequest request = findBorrowRequestById(requestId);

        if (request.getRequester().getId() != requesterId) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Only the requester can delete this request.");
        }
        borrowRequestRepo.delete(request);
    }

    @Transactional
    public List<BorrowRequest> getRequestsByRequester(int requesterId) {
        Player requester = playerRepo.findPlayerById(requesterId);
        if (requester == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("There is no requester with ID %d.",requesterId));
        }

        List<BorrowRequest> requests = borrowRequestRepo.findByRequester(requester);
        if (requests.isEmpty()) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "No borrow requests found for requester with ID " + requesterId);
        }
        
        return requests;
    }

    @Transactional
    public List<BorrowRequest> getRequestsByRequestee(int requesteeId) {
        Player requestee = playerRepo.findPlayerById(requesteeId);
        if (requestee == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("There is no requestee with ID %d.",requesteeId));
        }

        List<BorrowRequest> requests = borrowRequestRepo.findByRequestee(requestee);
        if (requests.isEmpty()) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "No borrow requests found for requestee with ID " + requesteeId);
        }
        
        return requests;
    }

    // Helper to validate request dto
    public void validateBorrowRequestCreationDto(BorrowRequestCreationDto dto) {
        if (dto.getComment().isBlank()) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Comment cannot be blank.");
        }
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "Start date must be in the future.");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BoardGameHubException(HttpStatus.BAD_REQUEST, "End date must be after start date.");
        }
    }
}
