package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.model.Game;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GameCopyRepository extends CrudRepository<GameCopy, Integer>{
    public GameCopy findGameCopyById(int id);
    public List<GameCopy> findByOwner(Player owner);
    public List<GameCopy> findByGame(Game game);
}