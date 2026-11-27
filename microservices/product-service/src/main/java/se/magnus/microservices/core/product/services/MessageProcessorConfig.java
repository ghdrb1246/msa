package se.magnus.microservices.core.product.services;

import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.event.Event;
import se.magnus.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
    private final ProductService productService;

    public MessageProcessorConfig(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    public Consumer<Event<Integer, Product>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    productService.createProduct(event.getData()).block();
                    break;
                case DELETE:
                    productService.deleteProduct(event.getKey()).block();
                    break;
                default:
                    throw new EventProcessingException("Incorrect event type: " +
                            event.getEventType() + ", expected a CREATE or DELETE event");
            }
        };
    }
}
