package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;
import ca.mcgill.ecse321.boardgamehub.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.dto.GameCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameUpdateDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class GameManagementService {
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameCopyRepository gameCopyRepo;
    @Autowired 
    private BorrowRequestRepository borrowRequestRepo;

    @Transactional
    public Game createGame(@Valid GameCreationDto gameToCreate){
        Game g = new Game(
            gameToCreate.getName(),
            gameToCreate.getMaxPlayers(),
            gameToCreate.getMinPlayers(),
            gameToCreate.getDescription()
        );
        return gameRepo.save(g);
    }

    @Transactional
    public void deletGame(int id ){
        Game g = gameRepo.findGameById(id);
        //make sure game exists
        if (g == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game does not exist");
        }
        else{
            //Delete all copies of the game
            for(GameCopy copy : gameCopyRepo.findAll()){
                if (copy.getGame().getId() == id){
                    gameCopyRepo.delete(copy);
                }
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

    public Player getOwnerById(int id){
        GameCopy copy = gameCopyRepo.findGameCopyById(id);
        if (copy == null){
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Game Copy does not exist");
        }
        else{
            return copy.getOwner();
        }
    }

    public Player getCurrentPlayerById(int id){
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
