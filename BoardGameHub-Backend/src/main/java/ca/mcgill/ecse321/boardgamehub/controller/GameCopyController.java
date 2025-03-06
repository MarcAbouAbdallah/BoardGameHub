package ca.mcgill.ecse321.boardgamehub.controller;

import ca.mcgill.ecse321.boardgamehub.middleware.RequireUser;
import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.response.GameCopyResponse;
import ca.mcgill.ecse321.boardgamehub.service.PersonalCollectionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gameCopies")
public class GameCopyController {

    private final PersonalCollectionService collectionService;

    @Autowired
    public GameCopyController(PersonalCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<List<GameCopyResponse>> getPersonalCollection(@PathVariable Integer playerId) {
        List<GameCopy> collection = collectionService.getPersonalCollection(playerId);
        List<GameCopyResponse> responses = collection.stream()
            .map(GameCopyResponse::fromGameCopy)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{playerId}/available")
    public ResponseEntity<List<GameCopyResponse>> getAvailableGameCopies(@PathVariable Integer playerId) {
        List<GameCopy> available = collectionService.getAvailableGames(playerId);
        List<GameCopyResponse> responses = available.stream()
            .map(GameCopyResponse::fromGameCopy)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{playerId}/add")
    @RequireUser
    public ResponseEntity<GameCopyResponse> addGameToPersonalCollection(@PathVariable Integer playerId,
                                                                        @RequestParam Integer gameId) {
        GameCopy newCopy = collectionService.addGameToPersonalCollection(playerId, gameId);
        return ResponseEntity.ok(GameCopyResponse.fromGameCopy(newCopy));
    }

    @DeleteMapping("/{playerId}/remove")
    @RequireUser
    public ResponseEntity<Void> removeGameFromPersonalCollection(@PathVariable Integer playerId,
                                                                 @RequestParam Integer gameId) {
        collectionService.removeGameFromPersonalCollection(playerId, gameId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playerId}/lend")
    @RequireUser
    public ResponseEntity<GameCopyResponse> lendGameCopy(@PathVariable Integer playerId,
                                                         @RequestParam Integer gameId) {
        GameCopy lentCopy = collectionService.lendGameCopy(playerId, gameId);
        return ResponseEntity.ok(GameCopyResponse.fromGameCopy(lentCopy));
    }

    @PostMapping("/{playerId}/return")
    @RequireUser
    public ResponseEntity<GameCopyResponse> returnGameCopy(@PathVariable Integer playerId,
                                                           @RequestParam Integer gameId) {
        GameCopy returnedCopy = collectionService.returnGameCopy(playerId, gameId);
        return ResponseEntity.ok(GameCopyResponse.fromGameCopy(returnedCopy));
    }
}