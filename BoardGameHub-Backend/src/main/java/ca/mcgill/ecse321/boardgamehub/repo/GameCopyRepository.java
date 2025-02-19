package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Game;
import ca.mcgill.ecse321.boardgamehub.model.Player;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameCopyRepository extends CrudRepository<GameCopy, Integer>{
    public GameCopy findGameCopyById(int id);
    public List<GameCopy> findByGame(Game game);
    public List<GameCopy> findByOwner(Player owner);
}