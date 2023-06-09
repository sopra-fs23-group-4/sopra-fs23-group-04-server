package ch.uzh.ifi.hase.soprafs23.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WebSocketService {
    @Autowired // Automatic injection of beans
    protected SimpMessagingTemplate simpMessagingTemplate;
    Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    public void sendMessageToClients(String destination, Object dto) {
        this.simpMessagingTemplate.convertAndSend(destination, dto);

        String logInfo = String.format("Msg sent off to: %s", destination);
        logger.info(logInfo);
    }

}
