package ca.mcgill.ecse321.boardgamehub.controller;

import ca.mcgill.ecse321.boardgamehub.middleware.RequireUser;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.service.PersonalCollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling a player's personal collection of GameCopies.
 */
@RestController
@RequestMapping("/gameCopies")
public class GameCopyController {

    private final PersonalCollectionService collectionService;

    @Autowired
    public GameCopyController(PersonalCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    /**
     * Retrieve the entire personal collection of a given player.
     * 
     * @param playerId The ID of the player
     * @return A list of GameCopyResponseDto objects
     */
    @GetMapping("/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getPersonalCollection(@PathVariable Integer playerId) {
        List<GameCopy> collection = collectionService.getPersonalCollection(playerId);
        return collection.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Retrieve only the available GameCopies in a player's collection.
     * 
     * @param playerId The ID of the player
     * @return A list of GameCopyResponseDto objects that are currently available
     */
    @GetMapping("/{playerId}/available")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getAvailableGameCopies(@PathVariable Integer playerId) {
        List<GameCopy> available = collectionService.getAvailableGames(playerId);
        return available.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Add a new GameCopy to the player's personal collection.
     * 
     * @param playerId The ID of the owner/player
     * @param gameId   The ID of the Game to add
     * @return The newly added GameCopy as a GameCopyResponseDto
     */
    @PostMapping("/{playerId}/add")
    @RequireUser
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addGameToPersonalCollection(
            @PathVariable Integer playerId, 
            @RequestParam Integer gameId) {

        GameCopy newCopy = collectionService.addGameToPersonalCollection(playerId, gameId);
        return GameCopyResponseDto.fromGameCopy(newCopy);
    }

    /**
     * Remove a GameCopy from the player's personal collection.
     * 
     * @param playerId The ID of the owner/player
     * @param gameId   The ID of the Game to remove
     */
    @DeleteMapping("/{playerId}/remove")
    @RequireUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameFromPersonalCollection(
            @PathVariable Integer playerId,
            @RequestParam Integer gameId) {

        collectionService.removeGameFromPersonalCollection(playerId, gameId);
    }

    /**
     * Lend out (make unavailable) a specific GameCopy.
     * 
     * @param playerId The ID of the owner/player
     * @param gameId   The ID of the Game to lend
     * @return The updated GameCopy as a GameCopyResponseDto
     */
    @PostMapping("/{playerId}/lend")
    @RequireUser
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto lendGameCopy(
            @PathVariable Integer playerId, 
            @RequestParam Integer gameId) {

        GameCopy lentCopy = collectionService.lendGameCopy(playerId, gameId);
        return GameCopyResponseDto.fromGameCopy(lentCopy);
    }

    /**
     * Return (make available) a specific GameCopy.
     * 
     * @param playerId The ID of the owner/player
     * @param gameId   The ID of the Game to return
     * @return The updated GameCopy as a GameCopyResponseDto
     */
    @PostMapping("/{playerId}/return")
    @RequireUser
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto returnGameCopy(
            @PathVariable Integer playerId, 
            @RequestParam Integer gameId) {

        GameCopy returnedCopy = collectionService.returnGameCopy(playerId, gameId);
        return GameCopyResponseDto.fromGameCopy(returnedCopy);
    }
}