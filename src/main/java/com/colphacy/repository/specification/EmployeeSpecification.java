package com.colphacy.repository.specification;
import com.colphacy.model.Employee;
import com.colphacy.model.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

public class EmployeeSpecification {

    public static Specification<Employee> filterBy(String keyword, Long branchId, Long roleId, Gender gender) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (gender != null && !gender.name().isEmpty()) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }

            if (roleId != null) {
                predicates.add(cb.equal(root.get("role").get("id"), roleId));
            }

            if (branchId != null) {
                predicates.add(cb.equal(root.get("branch").get("id"), branchId));
            }

            if (keyword != null && !keyword.isEmpty()) {
                Predicate fullNamePredicate = cb.like(root.get("fullName"), "%" + keyword + "%");
                Predicate usernamePredicate = cb.like(root.get("username"), "%" + keyword + "%");
                Predicate phonePredicate = cb.like(root.get("phone"), "%" + keyword + "%");
                predicates.add(cb.or(fullNamePredicate, usernamePredicate, phonePredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}