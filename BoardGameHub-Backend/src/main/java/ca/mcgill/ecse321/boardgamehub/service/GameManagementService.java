package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopyCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopyUpdateDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Validated
public class GameManagementService {
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;
    @Autowired 
    private BorrowRequestRepository borrowRequestRepo;

    @Transactional
    public Game createGame(@Valid GameCreationDto gameToCreate){
        if (gameToCreate.getMaxPlayers() >= gameToCreate.getMinPlayers()){
            Game g = new Game(
                gameToCreate.getName(),
                gameToCreate.getMaxPlayers(),
                gameToCreate.getMinPlayers(),
                gameToCreate.getDescription()
            );
            return gameRepo.save(g);
        }
        else{
            throw new BoardGameHubException(HttpStatus.FORBIDDEN, "Min players cannot exceed max players.");
        }
    }

    @Transactional
    public void deleteGame(int id ){
        Game g = gameRepo.findGameById(id);
        //make sure game exists
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game does not exist");
        }
        else{
            //Delete all copies of the game
            for(GameCopy copy : gameCopyRepo.findByGame(g)){
                gameCopyRepo.delete(copy);
            }
            //Once all copies are removed, remove the game
            gameRepo.delete(g);
        }
    }

    @Transactional 
    public Game updateGame(@Valid GameUpdateDto gameToUpdate, int id){
        Game g = findGameById(id);
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("No game has Id %d", id));
        }

        if (gameToUpdate.getName() != null){
            g.setName(gameToUpdate.getName());
        }
        if (gameToUpdate.getMaxPlayers() != null){
            g.setMaxPlayers(gameToUpdate.getMaxPlayers());
        }
        if (gameToUpdate.getMinPlayers() != null){
            g.setMinPlayers(gameToUpdate.getMinPlayers());
        }
        if (gameToUpdate.getDescription() != null){
            g.setDescription(gameToUpdate.getDescription());
        }

        return gameRepo.save(g);
    }

    public Game findGameById(int id){
        Game g = gameRepo.findGameById(id);
        //make sure game exists
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game does not exist");
        }
        else{
            return g;
        }
    }

    public List<Game> findGames(){
        return StreamSupport.stream(gameRepo.findAll().spliterator(), false)
                                .collect(Collectors.toList());

    }

    //GameCopy Stuff

    @Transactional
    public GameCopy createGameCopy(@Valid GameCopyCreationDto gameCopyToCreate){
        GameCopy g = new GameCopy(
            gameCopyToCreate.getIsAvailable(),
            gameCopyToCreate.getGame(),
            gameCopyToCreate.getOwner()
        );
        return gameCopyRepo.save(g);
    }

    @Transactional
    public void deleteGameCopy(int id ){
        GameCopy g = gameCopyRepo.findGameCopyById(id);
        //make sure gamecopy exists
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "GameCopy does not exist");
        }
        else{
            gameCopyRepo.delete(g);
        }
    }

    @Transactional 
    public GameCopy updateGameCopy(@Valid GameCopyUpdateDto gameCopyToUpdate, int id){
        GameCopy g = gameCopyRepo.findGameCopyById(id);
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, String.format("No gamecopy has Id %d", id));
        }

        if (gameCopyToUpdate.getGame() != null){
            g.setGame(gameCopyToUpdate.getGame());
        }
        if (gameCopyToUpdate.getOwner() != null){
            g.setOwner(gameCopyToUpdate.getOwner());
        }
        if (gameCopyToUpdate.getIsAvailable() != null){
            g.setIsAvailable(gameCopyToUpdate.getIsAvailable());
        }

        return gameCopyRepo.save(g);
    }

    public Player getOwnerById(int id){
        GameCopy copy = gameCopyRepo.findGameCopyById(id);
        if (copy == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game Copy does not exist");
        }
        else{
            return copy.getOwner();
        }
    }

    public Player getHolderById(int id){
        GameCopy copy = gameCopyRepo.findGameCopyById(id);
        if (copy == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game Copy does not exist");
        }
        else{
            //game being avaiable implies that whoever owns it currently has it
            if (copy.getIsAvailable()){
                return copy.getOwner();
            }
            else{
                //This may need to be updated to use the actual BorrowRequestService once that's implemented
                List<BorrowRequest> requests = borrowRequestRepo.findByRequestee(copy.getOwner());
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
                throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Current Holder of game not found");
            }
        }
    }
}
