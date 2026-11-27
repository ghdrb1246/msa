package se.magnus.api.composite.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummary {
    private int reviewId;
    private String author;
    private String subject;

    public ReviewSummary(int reviewId, String author, String subject) {
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
    }
}