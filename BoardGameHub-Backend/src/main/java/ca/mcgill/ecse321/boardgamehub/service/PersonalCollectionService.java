package ca.mcgill.ecse321.boardgamehub.service;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalCollectionService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GameCopyRepository gameCopyRepository;
    private final BorrowRequestRepository borrowRequestRepository;

    @Autowired
    public PersonalCollectionService(PlayerRepository playerRepository,
                                         GameRepository gameRepository,
                                         GameCopyRepository gameCopyRepository, BorrowRequestRepository borrowRequestRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameCopyRepository = gameCopyRepository;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<GameCopy> getPersonalCollection(Integer playerId) {
        Player player = findPlayerOrThrow(playerId);
        return gameCopyRepository.findByOwner(player);
    }

    @Transactional
    public GameCopy addGameToPersonalCollection(Integer playerId, Integer gameId) {
        Player player = findPlayerOrThrow(playerId);
        Game game = findGameOrThrow(gameId);

        // Check if the player already owns the game
        List<GameCopy> copies = gameCopyRepository.findByOwner(player);
        for (GameCopy copy : copies) {
            if (copy.getGame().getId() == game.getId()) {
                throw new IllegalStateException("Player already owns this game.");
            }
        }
        // Create and persist a new GameCopy with default availability true
        GameCopy newCopy = new GameCopy(true, game, player);
        return gameCopyRepository.save(newCopy);
    }

    @Transactional
    public void removeGameFromPersonalCollection(Integer playerId, Integer gameId) {
        Player player = findPlayerOrThrow(playerId);
        Game game = findGameOrThrow(gameId);

        // Locate the GameCopy to remove based on player and game
        List<GameCopy> copies = gameCopyRepository.findByOwner(player);
        GameCopy toRemove = null;
        for (GameCopy copy : copies) {
            if (copy.getGame().getId() == game.getId()) {
                toRemove = copy;
                break;
            }
        }
        if (toRemove != null) {
            gameCopyRepository.delete(toRemove);
        } else {
            throw new IllegalStateException("Game not found in player's collection.");
        }
    }

    @Transactional(readOnly = true)
    public List<GameCopy> getAvailableGames(Integer playerId) {
        // Filter player's collection for available game copies
        List<GameCopy> collection = getPersonalCollection(playerId);
        return collection.stream()
                .filter(GameCopy::getIsAvailable)
                .collect(Collectors.toList());
    }

    @Transactional
    public GameCopy lendGameCopy(Integer playerId, Integer gameId) {
        Player player = findPlayerOrThrow(playerId);
        Game game = findGameOrThrow(gameId);

        GameCopy target = findGameCopyInCollection(player, game);
        if (!target.getIsAvailable()) {
            throw new IllegalStateException("Game copy is already lent out.");
        }
        target.setIsAvailable(false);
        return gameCopyRepository.save(target);
    }

    @Transactional
    public GameCopy returnGameCopy(Integer playerId, Integer gameId) {
        Player player = findPlayerOrThrow(playerId);
        Game game = findGameOrThrow(gameId);

        GameCopy target = findGameCopyInCollection(player, game);
        if (target.getIsAvailable()) {
            throw new IllegalStateException("Game copy is already available.");
        }
        target.setIsAvailable(true);
        return gameCopyRepository.save(target);
    }

    // Helper to locate a game copy in a player's collection
    private GameCopy findGameCopyInCollection(Player player, Game game) {
        List<GameCopy> copies = gameCopyRepository.findByOwner(player);
        for (GameCopy copy : copies) {
            if (copy.getGame().getId() == game.getId()) {
                return copy;
            }
        }
        throw new IllegalStateException("Game not found in player's collection.");
    }

    private Player findPlayerOrThrow(Integer playerId) {
        return playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player with ID " + playerId + " not found."));
    }

    private Game findGameOrThrow(Integer gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game with ID " + gameId + " not found."));
    }

    public Player getOwnerById(int id){
        GameCopy copy = gameCopyRepository.findGameCopyById(id);
        if (copy == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game copy does not exist");
        }
        else{
            return copy.getOwner();
        }
    }

    public Player getHolderById(int id){
        GameCopy copy = gameCopyRepository.findGameCopyById(id);
        if (copy == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game copy does not exist");
        }
        else{
            //game being avaiable implies that whoever owns it currently has it
            if (copy.getIsAvailable()){
                return copy.getOwner();
            }
            else{
                //This may need to be updated to use the actual BorrowRequestService once that's implemented
                List<BorrowRequest> requests = borrowRequestRepository.findByRequestee(copy.getOwner());
                for (BorrowRequest borrowRequest : requests) {

                    //Set todays date
                    LocalDate today = LocalDate.now();
                    Date sqlDate = Date.valueOf(today);

                    /* Borrow requests are valid from the day they start so we need to 
                    check if the request started before tomorrow not before today */
                    Date sqlTomorrow = Date.valueOf(today.plusDays(1));

                    if (borrowRequest.getGame().getId() == id && borrowRequest.getEndDate().after(sqlDate) && borrowRequest.getStartDate().before(sqlTomorrow)){
                        return borrowRequest.getRequester();
                    }
                }
                //No valid borrowrequest was found
                throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Current holder of game not found");
            }
        }
    }
}