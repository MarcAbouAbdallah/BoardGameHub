package ca.mcgill.ecse321.boardgamehub.controller;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.service.PersonalCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players/{playerId}/gameCopies")
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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getPersonalCollection(@PathVariable int playerId) {
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
    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getAvailableGameCopies(@PathVariable int playerId) {
        List<GameCopy> available = collectionService.getAvailableGames(playerId);
        return available.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Create a new GameCopy in the player's personal collection by specifying a game ID.
     * This endpoint creates a new copy based on an existing game.
     *
     * @param playerId The ID of the owner/player
     * @param dto      A DTO carrying the ID of the Game to add
     * @return The newly added GameCopy as a GameCopyResponseDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addGameToPersonalCollection(
            @PathVariable int playerId,
            @RequestBody GameCopyResponseDto dto
    ) {
        GameCopy newCopy = collectionService.addGameToPersonalCollection(playerId, dto.getGameId());
        return GameCopyResponseDto.fromGameCopy(newCopy);
    }

    /**
     * Associate an already existing GameCopy with the player's collection.
     * This endpoint accepts a game copy ID and, if it is not already associated,
     * assigns it to the specified player.
     *
     * @param playerId   The ID of the owner/player
     * @param gameCopyId The ID of the existing GameCopy to add
     * @return The associated GameCopy as a GameCopyResponseDto
     */
    @PostMapping("/existing")
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addExistingGameCopyToPersonalCollection(
            @PathVariable int playerId,
            @RequestParam int gameCopyId
    ) {
        GameCopy updatedCopy = collectionService.associateExistingGameCopyToPersonalCollection(playerId, gameCopyId);
        return GameCopyResponseDto.fromGameCopy(updatedCopy);
    }

    /**
     * Remove a GameCopy from the player's personal collection by game copy ID.
     *
     * @param playerId   The ID of the owner/player
     * @param gameCopyId The ID of the GameCopy to remove
     */
    @DeleteMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameFromPersonalCollection(
            @PathVariable int playerId,
            @PathVariable int gameCopyId
    ) {
        collectionService.removeGameFromPersonalCollection(playerId, gameCopyId);
    }

    /**
     * Update the availability of a specific GameCopy using its game copy ID.
     * If isAvailable = false, the game copy is lent out; if true, it is returned.
     *
     * @param playerId   The ID of the owner/player
     * @param gameCopyId The ID of the GameCopy to update
     * @param dto        A DTO containing the new availability status
     * @return The updated GameCopy as a GameCopyResponseDto
     */
    @PatchMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto updateGameAvailability(
            @PathVariable int playerId,
            @PathVariable int gameCopyId,
            @RequestBody GameCopyResponseDto dto
    ) {
        boolean isAvailable = dto.getIsAvailable();
        GameCopy updated;
        if (isAvailable) {
            updated = collectionService.returnGameCopy(playerId, gameCopyId);
        } else {
            updated = collectionService.lendGameCopy(playerId, gameCopyId);
        }
        return GameCopyResponseDto.fromGameCopy(updated);
    }
}