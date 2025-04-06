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

    @Autowired
    private PersonalCollectionService collectionService;

    /**
     * Retrieve the entire personal collection of a given player.
     *
     * @param searchDto A DTO carrying the playerId.
     * @return A list of GameCopyResponseDto objects
     */
    @GetMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GameCopyResponseDto> getPersonalCollection(@PathVariable int playerId) {
        List<GameCopy> collection = collectionService.getPersonalCollection(playerId);
        return collection.stream()
            .map(gameCopy -> new GameCopyResponseDto(gameCopy, collectionService.isAvailable(gameCopy)))
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
            .map(gameCopy -> new GameCopyResponseDto(gameCopy, collectionService.isAvailable(gameCopy)))
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
            .map(gameCopy -> new GameCopyResponseDto(gameCopy, collectionService.isAvailable(gameCopy)))
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
        return new GameCopyResponseDto(copy, collectionService.isAvailable(copy));
    }

    /**
     * Create a new GameCopy in the player's personal collection by specifying a
     * game ID.
     * This endpoint creates a new copy based on an existing game.
     *
     * @param creationDto A DTO carrying the playerId and the gameId to add.
     * @return The newly added GameCopy as a GameCopyResponseDto.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameCopyResponseDto addGameToPersonalCollection(@RequestBody GameCopyCreationDto creationDto) {
        GameCopy newCopy = collectionService.addGameToPersonalCollection(creationDto.getPlayerId(), creationDto.getGameId());
        return new GameCopyResponseDto(newCopy, collectionService.isAvailable(newCopy));
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