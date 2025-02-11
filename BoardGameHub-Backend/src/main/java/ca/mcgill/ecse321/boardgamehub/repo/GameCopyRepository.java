package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.GameCopy;
import org.springframework.data.repository.CrudRepository;

public interface GameCopyRepository extends CrudRepository<GameCopy, Integer>{
    
}