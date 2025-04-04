package ca.mcgill.ecse321.boardgamehub.service;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Registration;
import ca.mcgill.ecse321.boardgamehub.model.Event;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.EventRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import ca.mcgill.ecse321.boardgamehub.repo.RegistrationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
//import java.util.stream.Collectors;

@Service
@Validated
public class PersonalCollectionService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameCopyRepository gameCopyRepository;
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Transactional(readOnly = true)
    public List<GameCopy> getPersonalCollection(int playerId) {
        Player player = findPlayerOrThrow(playerId);
        return gameCopyRepository.findByOwner(player);
    }

    @Transactional
    public GameCopy addGameToPersonalCollection(int playerId, int gameId) {
        Player player = findPlayerOrThrow(playerId);
        Game game = findGameOrThrow(gameId);

        // If owner currently has no games, set game owner to true
        List<GameCopy> copies = gameCopyRepository.findByOwner(player);
        if (copies.isEmpty() && player.getIsGameOwner() == false) {
            player.setIsGameOwner(true);
            playerRepository.save(player);
        } else {
            //Check if owner already has game
            for (GameCopy copy : copies) {
                if (copy.getGame().getId() == game.getId()) {
                    throw new BoardGameHubException(HttpStatus.BAD_REQUEST,
                        "Player already owns this game.");
                }
            }
        }
        // Create and persist a new GameCopy with default availability true
        GameCopy newCopy = new GameCopy(game, player);
        return gameCopyRepository.save(newCopy);
    }

    @Transactional
    public void removeGameCopy(int userId, int gameCopyId) {
        // Retrieve the game copy to remove
        GameCopy toRemove = findGameCopyOrThrow(gameCopyId);
        Player player = findPlayerOrThrow(userId);

        // Permission check: Only the owner can delete
        if (toRemove.getOwner() == null || toRemove.getOwner().getId() != userId) {
            throw new BoardGameHubException(HttpStatus.FORBIDDEN,
                    "You are not the owner of this game copy. Deletion is not allowed.");
        }

        List<BorrowRequest> requests = borrowRequestRepository.findByGame(toRemove);
        for (BorrowRequest b: requests) {
            borrowRequestRepository.delete(b);
        }

        List<Event> events = eventRepository.findByGame(toRemove);
        for (Event e: events) {
            for (Registration r: registrationRepository.findByKey_RegisteredEvent(e)) {
                registrationRepository.delete(r);
            }
            eventRepository.delete(e);
        }

        gameCopyRepository.delete(toRemove);

        List<GameCopy> copies = gameCopyRepository.findByOwner(player);
        if (copies.isEmpty() && player.getIsGameOwner() == true) {
            player.setIsGameOwner(false);
            playerRepository.save(player);
        }
    }

    @Transactional(readOnly = true)
    public List<GameCopy> getAvailableGames(int playerId) {
        // Filter player's collection for available game copies
        List<GameCopy> collection = getPersonalCollection(playerId);
        List<GameCopy> available = new ArrayList<>();
        for (GameCopy c: collection) {
            if (isAvailable(c)) available.add(c);
        }
        return available;

    }

    @Transactional(readOnly = true)
    public List<GameCopy> getGameCopiesForGame(int gameId) {
        Game game = findGameOrThrow(gameId);
        return gameCopyRepository.findByGame(game);
    }

    /*
     * The following service method is not in use due to changes in the way we check for availability and return games
     */

    // @Transactional
    // public GameCopy lendGameCopy(int playerId, int gameCopyId) {
    //     Player player = findPlayerOrThrow(playerId);
    //     GameCopy target = findGameCopyOrThrow(gameCopyId);

    //     if (target.getOwner().getId() != player.getId()) {
    //         throw new BoardGameHubException(HttpStatus.BAD_REQUEST,
    //                 "Game copy does not belong to the specified player.");
    //     }
    //     if (!target.getIsAvailable()) {
    //         throw new BoardGameHubException(HttpStatus.BAD_REQUEST,
    //                 "Game copy is already lent out.");
    //     }

    //     target.setIsAvailable(false);
    //     return gameCopyRepository.save(target);
    // }


    /*
     * The following service method is not in use due to changes in the way we check for availability and return games
     * THIS ONE MIGHT BE USED IF RETURN FUNCTIONALITY IS LATER IMPLEMENTED
     */

    // @Transactional
    // public GameCopy returnGameCopy(int playerId, int gameCopyId) {
    //     Player player = findPlayerOrThrow(playerId);
    //     GameCopy target = findGameCopyOrThrow(gameCopyId);

    //     if (target.getOwner().getId() != player.getId()) {
    //         throw new BoardGameHubException(HttpStatus.BAD_REQUEST,
    //                 "Game copy does not belong to the specified player.");
    //     }
    //     if (target.getIsAvailable()) {
    //         throw new BoardGameHubException(HttpStatus.BAD_REQUEST,
    //                 "Game copy is already available.");
    //     }

    //     target.setIsAvailable(true);
    //     return gameCopyRepository.save(target);
    // }

    @Transactional(readOnly = true)
    public GameCopy getGameCopyById(int gameCopyId) {
        return findGameCopyOrThrow(gameCopyId);
    }

    private GameCopy findGameCopyOrThrow(int gameCopyId) {
        return gameCopyRepository.findById(gameCopyId)
            .orElseThrow(() -> new BoardGameHubException(
                HttpStatus.NOT_FOUND,
                String.format("Game copy with ID %d not found.", gameCopyId)));
    }

    private Player findPlayerOrThrow(int playerId) {
        return playerRepository.findById(playerId)
            .orElseThrow(() -> new BoardGameHubException(
                HttpStatus.NOT_FOUND,
                String.format("Player with ID %d not found.", playerId)));
    }

    private Game findGameOrThrow(int gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new BoardGameHubException(
                HttpStatus.NOT_FOUND,
                String.format("Game with ID %d not found.", gameId)));
    }

    /*
     * New method to check if a particular game copy is available by checking the current date against accepted borrow requests
     */
    public boolean isAvailable(GameCopy copy) {
        List<BorrowRequest> list = borrowRequestRepository.findByGame(copy);
        Date now = Date.valueOf(LocalDate.now());
        for (BorrowRequest b: list) {
            if (b.getStatus() != BorrowStatus.ACCEPTED) continue;
            if (b.getStartDate().equals(now) || b.getEndDate().equals(now) || (b.getStartDate().before(now) && b.getEndDate().after(now))) {
                return false;
            }
        }
        return true;

    }
}