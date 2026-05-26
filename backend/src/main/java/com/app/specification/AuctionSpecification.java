package com.app.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.app.dto.request.auction.AuctionSearchRequest;
import com.app.entity.auction.AuctionEntity;

public class AuctionSpecification {
    
    public static Specification<AuctionEntity> search(AuctionSearchRequest req){

        return new Specification<AuctionEntity>() { // Trả về object mô tả điều kiện WHERE
            
            @Override
            public Predicate toPredicate( // 1 điều kiện WHERE
                    Root<AuctionEntity> root, // Table auction
                    CriteriaQuery<?> query,
                    CriteriaBuilder cb){ // Object build SQL

                List<Predicate> predicates = new ArrayList<>(); // Chứa toàn bộ điều kiện WHERE

                // ====== KEYWORD ======
                if (req.getKeyword() != null && !req.getKeyword().isBlank()){ // Nếu nhập keyword thì add điều kiện còn không bỏ qua

                    // Tạo predicate 
                    Predicate keywordPredicate = cb.like(cb.lower(root.get("title")), "%" + req.getKeyword().toLowerCase() + "%");

                    // Thêm WHERE vào list
                    predicates.add(keywordPredicate);
                }

                // ====== STATUS ======
                if (req.getAuctionStatus() != null){

                    Predicate statusPredicate = cb.equal(root.get("status"), req.getAuctionStatus());

                    predicates.add(statusPredicate);
                }

                // ====== ITEM TYPE ======
                if (req.getItemType() != null){

                    Predicate itemTypePredicate = cb.equal(root.get("item").get("itemType"), req.getItemType());

                    predicates.add(itemTypePredicate);
                }

                // ====== MIN PRICE ======
                if (req.getMinPrice() != null){

                    Predicate minPricePredicate = cb.greaterThanOrEqualTo(root.get("currentPrice").get("value"), req.getMinPrice());

                    predicates.add(minPricePredicate);
                }

                // ====== MAX PRICE ======
                if (req.getMaxPrice() != null){

                    Predicate maxPricePredicate = cb.lessThanOrEqualTo(root.get("currentPrice").get("value"), req.getMaxPrice());

                    predicates.add(maxPricePredicate);
                }

                // ====== SELLER ======
                if (req.getSellerId() != null){

                    Predicate sellerPredicate = cb.equal(root.get("seller").get("userId"), req.getSellerId());

                    predicates.add(sellerPredicate);
                }

                // Nối tất cả WHERE bằng AND và convert sang Array
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
