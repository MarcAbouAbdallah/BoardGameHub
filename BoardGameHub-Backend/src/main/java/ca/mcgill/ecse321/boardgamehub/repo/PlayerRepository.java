package ca.mcgill.ecse321.boardgamehub.repo;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.boardgamehub.model.Player;


public interface PlayerRepository extends CrudRepository<Player, Integer> {
    public Player findPlayerById(UUID id);
}
