package ca.mcgill.ecse321.boardgamehub.controller;

import ca.mcgill.ecse321.boardgamehub.dto.GameCopyCreationDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopyResponseDto;
import ca.mcgill.ecse321.boardgamehub.dto.GameCopySearchDto;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.service.PersonalCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/gamecopies")
public class GameCopyController {

    private final PersonalCollectionService collectionService;

    @Autowired
    public GameCopyController(PersonalCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    /**
     * Retrieve the entire personal collection of a given player.
     *
     * @param searchDto A DTO carrying the playerId.
     * @return A list of GameCopyResponseDto objects
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getPersonalCollection(@RequestBody GameCopySearchDto searchDto) {
        List<GameCopy> collection = collectionService.getPersonalCollection(searchDto.getPlayerId());
        return collection.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Retrieve only the available GameCopies in a player's collection.
     *
     * @param searchDto A DTO carrying the playerId.
     * @return A list of GameCopyResponseDto objects that are currently available
     */
    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getAvailableGameCopies(@RequestBody GameCopySearchDto searchDto) {
        List<GameCopy> available = collectionService.getAvailableGames(searchDto.getPlayerId());
        return available.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Get all game copies for a particular game with given id.
     *
     * @param id The numerical unique Id of the game whose copies we want to find.
     * @return A list of GameCopyResponseDto objects whose game we are looking for.
     */
    @GetMapping("/games/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getGameCopiesForGame(@PathVariable int id) {
        List<GameCopy> copies = collectionService.getGameCopiesForGame(id);
        return copies.stream()
            .map(GameCopyResponseDto::fromGameCopy)
            .toList();
    }

    /**
     * Retrieve a specific GameCopy by its ID.
     *
     * @param gameCopyId The ID of the GameCopy to retrieve.
     * @return The GameCopy as a GameCopyResponseDto.
     */
    @GetMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto getGameCopyById(@PathVariable int gameCopyId) {
        GameCopy copy = collectionService.getGameCopyById(gameCopyId);
        return GameCopyResponseDto.fromGameCopy(copy);
    }

    /**
     * Create a new GameCopy in the player's personal collection by specifying a game ID.
     * This endpoint creates a new copy based on an existing game.
     *
     * @param creationDto A DTO carrying the playerId and the gameId to add.
     * @return The newly added GameCopy as a GameCopyResponseDto.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addGameToPersonalCollection(@RequestBody GameCopyCreationDto creationDto) {
        GameCopy newCopy = collectionService.addGameToPersonalCollection(creationDto.getPlayerId(), creationDto.getGameId());
        return GameCopyResponseDto.fromGameCopy(newCopy);
    }

    /**
     * Update the availability of a specific GameCopy using its game copy ID.
     * If isAvailable = false, the game copy is lent out; if true, it is returned.
     *
     * @param gameCopyId The ID of the GameCopy to update.
     * @param dto        A DTO containing the new availability status and the ownerId (for permission checking).
     * @return The updated GameCopy as a GameCopyResponseDto.
     */
    @PatchMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.OK)
    public GameCopyResponseDto updateGameAvailability(
            @PathVariable int gameCopyId,
            @RequestBody GameCopyResponseDto dto
    ) {
        int playerId = dto.getOwnerId();
        boolean isAvailable = dto.getIsAvailable();
        GameCopy updated;
        if (isAvailable) {
            updated = collectionService.returnGameCopy(playerId, gameCopyId);
        } else {
            updated = collectionService.lendGameCopy(playerId, gameCopyId);
        }
        return GameCopyResponseDto.fromGameCopy(updated);
    }

    /**
     * Remove a GameCopy from the player's personal collection by game copy ID.
     *
     * @param gameCopyId The ID of the GameCopy to remove.
     */
    @DeleteMapping("/{gameCopyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameFromPersonalCollection(@PathVariable int gameCopyId,
                                                @RequestParam int userId) {
        collectionService.removeGameCopy(userId, gameCopyId);
    }
}