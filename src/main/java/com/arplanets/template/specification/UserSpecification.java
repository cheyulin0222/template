package com.arplanets.template.specification;

import com.arplanets.template.domain.User;
import com.arplanets.template.dto.UserServiceDto;
import com.arplanets.template.req.UserSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification {

    public Specification<User> createSpecification(UserServiceDto request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Email 模糊查詢
            if (StringUtils.hasText(request.getEmail())) {
                // root 為 User 的實體
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }

            // equal
//            if (StringUtils.hasText(request.getEmail())) {
//                // root 為 User 的實體
//                predicates.add(criteriaBuilder.equal(root.get("email"), request.getEmail()));
//            }

            // 年齡範圍
            if (request.getAdvancedSearchInfo().getAgeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("age"), request.getAdvancedSearchInfo().getAgeStart()));
            }
            if (request.getAdvancedSearchInfo().getAgeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("age"), request.getAdvancedSearchInfo().getAgeEnd()));
            }

            // 創建時間範圍
            if (request.getAdvancedSearchInfo().getCreatedAtStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getAdvancedSearchInfo().getCreatedAtStart()));
            }
            if (request.getAdvancedSearchInfo().getCreatedAtEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getAdvancedSearchInfo().getCreatedAtEnd()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
