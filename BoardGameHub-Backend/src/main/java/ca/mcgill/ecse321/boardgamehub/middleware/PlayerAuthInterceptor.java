package ca.mcgill.ecse321.boardgamehub.middleware;

import ca.mcgill.ecse321.boardgamehub.exception.UnauthedException;
import ca.mcgill.ecse321.boardgamehub.model.Player;
import ca.mcgill.ecse321.boardgamehub.repo.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PlayerAuthInterceptor implements HandlerInterceptor {

    private final PlayerRepository playerRepository;
    private final PlayerContext playerContext;

    @Autowired
    public PlayerAuthInterceptor(PlayerRepository playerRepository,
                                 PlayerContext playerContext) {
        this.playerRepository = playerRepository;
        this.playerContext = playerContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws UnauthedException {

        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequireUser requireUser = method.getMethodAnnotation(RequireUser.class);
        if (requireUser == null) {
            requireUser = method.getBeanType().getAnnotation(RequireUser.class);
        }

        if (requireUser != null) {
            String userIdHeader = request.getHeader("User-Id");
            if (userIdHeader == null) {
                throw new UnauthedException("No User-Id header provided");
            }

            try {
                int playerId = Integer.parseInt(userIdHeader);
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new UnauthedException("Player not found"));
                playerContext.setCurrentPlayer(player);

            } catch (NumberFormatException e) {
                throw new UnauthedException("Invalid User-Id format: must be an integer");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        playerContext.clear();
    }
}