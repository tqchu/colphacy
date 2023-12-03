package com.colphacy.mapper;

import com.colphacy.dto.review.ReviewCustomerListDTO;
import com.colphacy.model.Review;
import com.colphacy.repository.ReviewRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "reviewerName", expression = "java(getReviewerName(review))")
    @Mapping(target = "childReview", source = "review", qualifiedByName = "mapChildReview")
    ReviewCustomerListDTO reviewToReviewCustomerListDTO(Review review, @Context ReviewRepository reviewRepository);

    @Named("mapChildReview")
    default ReviewCustomerListDTO mapChildReview(Review review, @Context ReviewRepository reviewRepository) {
        Review childReview = reviewRepository.findByParentReviewId(review.getId());
        return reviewToReviewCustomerListDTO(childReview, reviewRepository);
    }

    default String getReviewerName(Review review) {
        if (review.getEmployee() != null) {
            return review.getEmployee().getFullName();
        }
        return review.getCustomer().getFullName();
    }
}
