package se.magnus.microservices.composite.product.services;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    public ProductCompositeIntegration(WebClient.Builder webClient, ObjectMapper mapper,
                                       @Value("${app.product-service.host}") String productServiceHost,
                                       @Value("${app.product-service.port}") int productServicePort,
                                       @Value("${app.recommendation-service.host}") String recommendationServiceHost,
                                       @Value("${app.recommendation-service.port}") int recommendationServicePort,
                                       @Value("${app.review-service.host}") String reviewServiceHost,
                                       @Value("${app.review-service.port}") int reviewServicePort) {
        this.webClient = webClient.build();
        this.mapper = mapper;
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort;
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort;
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort;
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        String url = productServiceUrl + "/product/" + productId;
        return webClient.get().uri(url).retrieve().bodyToMono(Product.class)
                .log(LOG.getName(), FINE)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        String url = recommendationServiceUrl + "/recommendation?productId=" + productId;
        return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Review> getReviews(int productId) {
        String url = reviewServiceUrl + "/review?productId=" + productId;
        return webClient.get().uri(url).retrieve().bodyToFlux(Review.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Product> createProduct(Product body) {
        return webClient.post().uri(productServiceUrl + "/product").bodyValue(body)
                .retrieve().bodyToMono(Product.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation body) {
        return webClient.post().uri(recommendationServiceUrl + "/recommendation").bodyValue(body)
                .retrieve().bodyToMono(Recommendation.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Mono<Review> createReview(Review body) {
        return webClient.post().uri(reviewServiceUrl + "/review").bodyValue(body)
                .retrieve().bodyToMono(Review.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        return webClient.delete().uri(productServiceUrl + "/product/" + productId)
                .retrieve().bodyToMono(Void.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        return webClient.delete().uri(recommendationServiceUrl + "/recommendation?productId=" + productId)
                .retrieve().bodyToMono(Void.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    @Override
    public Mono<Void> deleteReviews(int productId) {
        return webClient.delete().uri(reviewServiceUrl + "/review?productId=" + productId)
                .retrieve().bodyToMono(Void.class)
                .onErrorMap(WebClientResponseException.class, this::handleException);
    }

    private Throwable handleException(WebClientResponseException ex) {
        int status = ex.getStatusCode().value();
        if (status == 404) return new NotFoundException(getErrorMessage(ex));
        if (status == 422) return new InvalidInputException(getErrorMessage(ex));
        return ex;
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (JacksonException jsonEx) {
            return ex.getMessage();
        }
    }
}