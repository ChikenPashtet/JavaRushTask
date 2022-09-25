package com.game.specification;

import com.game.entity.Player;
import com.game.entity.PlayerWithAllParams;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerSpecification {
    public static Specification<Player> checkParams(PlayerWithAllParams player) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            if (player.getName() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + player.getName() + "%"));
            }
            if (player.getTitle() != null) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + player.getTitle() + "%"));
            }
            if (player.getBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(player.getBefore())));
            }
            if (player.getAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(player.getAfter())));
            }
            if (player.getMinExperience() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), player.getMinExperience()));
            }
            if (player.getMaxExperience() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("experience"), player.getMaxExperience()));
            }
            if (player.getMinLevel() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("level"), player.getMinLevel()));
            }
            if (player.getMaxLevel() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("level"), player.getMaxLevel()));
            }
            if (player.getRace() != null) {
                predicates.add(criteriaBuilder.equal(root.get("race"), player.getRace()));
            }
            if (player.getProfession() != null) {
                predicates.add(criteriaBuilder.equal(root.get("profession"), player.getProfession()));
            }
            if (player.getBanned() != null) {
                predicates.add(criteriaBuilder.equal(root.get("banned"), player.getBanned()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
