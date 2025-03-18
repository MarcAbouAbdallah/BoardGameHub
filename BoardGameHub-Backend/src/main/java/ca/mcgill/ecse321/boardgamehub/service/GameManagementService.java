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

        gameToUpdate.validate();

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
}
