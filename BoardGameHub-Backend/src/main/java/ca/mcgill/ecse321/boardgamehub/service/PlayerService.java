package ca.mcgill.ecse321.boardgamehub.service;

import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;

import ca.mcgill.ecse321.boardgamehub.model.Player;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerLoginDto;
import ca.mcgill.ecse321.boardgamehub.dto.PlayerUpdateDto;
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;

import java.util.List;


@Service
@Validated
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    
    @Transactional
    public Player registerPlayer(@Valid PlayerCreationDto playerCreationDto) {

        List<Player> players = (List<Player>) playerRepository.findAll();

        for (Player p : players) {
            if (p.getEmail().equals(playerCreationDto.getEmail())) {
                throw new BoardGameHubException(HttpStatus.CONFLICT, "Email already in use");
            }
        }

        Player PlayerToRegister = new Player(
            playerCreationDto.getName(),
            playerCreationDto.getEmail(),
            playerCreationDto.getPassword(),
            playerCreationDto.getIsGameOwner()
        );
        return playerRepository.save(PlayerToRegister);
    }

    @Transactional
    public Player login(@Valid PlayerLoginDto playerLoginDto) {
        Player player = playerRepository.findPlayerByEmail(playerLoginDto.getEmail());
        if (player == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Incorrect email or account does not exist");
        }
        if (!player.getPassword().equals(playerLoginDto.getPassword())) {
            throw new BoardGameHubException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        return player;
        
    }

    @Transactional
    public void deletePlayer(int id) {
        Player p = playerRepository.findPlayerById(id);
        if (p == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Player does not exist");
        }
        playerRepository.delete(p);
        
    }

    @Transactional
    public Player getPlayerById(int id) {
        Player p = playerRepository.findPlayerById(id);
        if (p == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Player does not exist");
        }
        return p;
        
    }

    @Transactional
    public Player updatePlayer(int id, @Valid PlayerUpdateDto playerUpdateDto) {
        Player p = playerRepository.findPlayerById(id);
        if (p == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Player does not exist");
        }

        List<Player> players = (List<Player>) playerRepository.findAll();
        
        for (Player existingPlayer : players) {
            if (existingPlayer.getEmail().equals(playerUpdateDto.getEmail())) {
                throw new BoardGameHubException(HttpStatus.CONFLICT, "Email already in use");
            }
        }

        playerUpdateDto.validate();

        if (playerUpdateDto.getName() != null) {
            p.setName(playerUpdateDto.getName());
        }
        if (playerUpdateDto.getEmail() != null) {
            p.setEmail(playerUpdateDto.getEmail());
        }
        if (playerUpdateDto.getPassword() != null) {
            p.setPassword(playerUpdateDto.getPassword());
        }
        if (playerUpdateDto.getIsGameOwner() != null) {
            p.setIsGameOwner(playerUpdateDto.getIsGameOwner());
        }
        return playerRepository.save(p);
    }
    

}