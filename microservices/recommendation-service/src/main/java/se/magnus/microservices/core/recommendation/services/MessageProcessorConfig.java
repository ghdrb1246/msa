package se.magnus.microservices.core.recommendation.services;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
    private final RecommendationService recommendationService;

    public MessageProcessorConfig(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Bean
    public Consumer<Event<Integer, Recommendation>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    recommendationService.createRecommendation(event.getData()).block();
                    break;
                case DELETE:
                    recommendationService.deleteRecommendations(event.getKey()).block();
                    break;
                default:
                    throw new EventProcessingException("Incorrect event type: " +
                            event.getEventType() + ", expected a CREATE or DELETE event");
            }
        };
    }
}
