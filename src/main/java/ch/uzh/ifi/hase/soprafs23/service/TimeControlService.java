package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeControlService {
    private final GameRepository gameRepository;
    private final WebSocketService webSocketService;

    public TimeControlService(@Qualifier("gameRepository") GameRepository gameRepository, WebSocketService webSocketService){
        this.gameRepository=gameRepository;
        this.webSocketService=webSocketService;

    }

//todo move time control from roundservice and voteservice to timecontrol
// todo make sure to also

}
