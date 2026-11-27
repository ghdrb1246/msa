package se.magnus.api.composite.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationSummary {
    private int recommendationId;
    private String author;
    private int rate;

    public RecommendationSummary(int recommendationId, String author, int rate) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
    }
}