package ch.uzh.ifi.hase.soprafs23.webSockets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Set prefix for the endpoint that the client listens for our messages from
        registry.enableSimpleBroker("/topic", "/queue", "/user")
                .setTaskScheduler(heartBeatScheduler());

        // Set prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/app");

        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the endpoint where the connection will take place
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .withSockJS();
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
