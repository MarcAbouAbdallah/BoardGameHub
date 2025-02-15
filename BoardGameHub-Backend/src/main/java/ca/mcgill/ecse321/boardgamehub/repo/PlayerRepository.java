package ca.mcgill.ecse321.boardgamehub.repo;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.boardgamehub.model.Player;


public interface PlayerRepository extends CrudRepository<Player, Integer> {
    public Player findPlayerById(int id);

    public Player findPlayerByEmail(String email);
}
