package ca.mcgill.ecse321.boardgamehub.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import ca.mcgill.ecse321.boardgamehub.repo.GameCopyRepository;
import ca.mcgill.ecse321.boardgamehub.repo.GameRepository;

import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
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
}
