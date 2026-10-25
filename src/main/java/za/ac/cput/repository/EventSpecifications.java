package za.ac.cput.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;

import java.util.ArrayList;
import java.util.List;

public final class EventSpecifications {

    private EventSpecifications() {
    }

    public static Specification<Event> withFilters(String search,
                                                   EventStatusEnum status,
                                                   String fromBound,
                                                   String toBound) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (fromBound != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), fromBound));
            }

            if (toBound != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), toBound));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
