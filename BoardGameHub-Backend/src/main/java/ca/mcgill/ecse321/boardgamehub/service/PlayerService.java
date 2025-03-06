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
import ca.mcgill.ecse321.boardgamehub.exception.BoardGameHubException;


@Service
@Validated
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    



    @Transactional
    public Player RegisterPlayer(@Valid PlayerCreationDto playerCreationDto) {
        Player p = new Player(
            playerCreationDto.getName(),
            playerCreationDto.getEmail(),
            playerCreationDto.getPassword(),
            playerCreationDto.getIsGameOwner()
        );
        return playerRepository.save(p);
    }

    @Transactional
    public Player Login(@Valid PlayerLoginDto playerLoginDto) {
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
    public void DeletePlayer(int id) {
        Player p = playerRepository.findPlayerById(id);
        if (p == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Player does not exist");
        }
        playerRepository.delete(p);
        
    }

    @Transactional
    public Player RetrievePlayer(int id) {
        Player p = playerRepository.findPlayerById(id);
        if (p == null) {
            throw new BoardGameHubException(HttpStatus.NOT_FOUND, "Player does not exist");
        }
        return p;
        
    }

}