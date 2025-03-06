package ca.mcgill.ecse321.boardgamehub.service;

import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;

import ca.mcgill.ecse321.boardgamehub.model.Player;

import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    
    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player Register(String name, String email, String password) {
        //Check if name, email, and password are valid
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }
        
        //Create a new player
        try {
            Player PlayerToCreate = new Player(name, email, password, false);
            playerRepository.save(PlayerToCreate);
            return PlayerToCreate;
        }catch (Exception e) {
            throw new IllegalTransactionStateException(e.getMessage());
        }

    }

    @Transactional
    public Player Login(String email, String password) {
        //Check if email and password are valid
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }

        //Check if the player exists
        Player player = playerRepository.findPlayerByEmail(email);
        if (player == null) {
            throw new IllegalArgumentException("Player does not exist!");
        }

        //Check if the password is correct
        if (!player.getPassword().equals(password)) {
            throw new IllegalArgumentException("Incorrect password!");
        }

        return player;
    }

    @Transactional
    public boolean DeletePlayer(String email) {
        //Check if email is valid
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }

        //Check if the player exists
        Player player = playerRepository.findPlayerByEmail(email);
        if (player == null) {
            throw new IllegalArgumentException("Player does not exist!");
        }

        //Delete the player
        try {
            playerRepository.delete(player);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return true;
    }

    @Transactional
    public Player RetrievePlayer(String email) {
        //Check if email is valid
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }

        //Check if the player exists
        Player player = playerRepository.findPlayerByEmail(email);
        if (player == null) {
            throw new IllegalArgumentException("Player does not exist!");
        }

        return player;
    }

}