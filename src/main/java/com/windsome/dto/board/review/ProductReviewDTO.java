package com.windsome.dto.board.review;

import com.windsome.entity.Product;
import com.windsome.entity.board.Review;
import lombok.Data;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Data
public class ProductReviewDTO {

    private Long reviewId; // 리뷰 id

    private String title; // 제목

    private String regDate; // 작성일

    private String createdBy; // 작성자

    private BigDecimal rating; // 평점

    private int hits; // 조회수

    public static ProductReviewDTO createProductReviewDTO(Review review, Product product) {
        ProductReviewDTO productReviewDTO = new ProductReviewDTO();
        productReviewDTO.setReviewId(review.getId());
        productReviewDTO.setTitle(review.getTitle());
        productReviewDTO.setRegDate(review.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        productReviewDTO.setCreatedBy(review.getMember().getName());
        productReviewDTO.setRating(review.getRating());
        productReviewDTO.setHits(review.getHits());
        return productReviewDTO;
    }
}