package ca.mcgill.ecse321.boardgamehub.repo;

import ca.mcgill.ecse321.boardgamehub.model.BorrowRequest;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BorrowRequestRepository extends CrudRepository<BorrowRequest, Integer> {
    public BorrowRequest findBorrowRequestById(int id);
    public List<BorrowRequest> findByRequester(Player requester);
    public List<BorrowRequest> findByRequestee(Player requestee);
}

