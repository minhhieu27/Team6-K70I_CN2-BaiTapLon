package com.app.service.auction;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.app.common.money.Money;
import com.app.dto.request.auction.AuctionSearchRequest;
import com.app.dto.response.auction.AuctionResponse;
import com.app.entity.auction.AuctionEntity;
import com.app.exception.auction.AuctionNotFoundException;
import com.app.mapper.AuctionMapper;
import com.app.repository.AuctionRepository;
import com.app.specification.AuctionSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionQuerryService {
    
    private final AuctionRepository auctionRepository;

    private final AuctionMapper auctionMapper;

    // ====== GET ALL ======
    public List<AuctionResponse> getAll(){
        return auctionMapper.toResponseList(auctionRepository.findAll());
    }

    // ====== GET BY ID ======
    public AuctionResponse getByAuctionId(String auctionId){
        return auctionMapper.toResponse(getEntityByAuctionId(auctionId));
    }

    // ====== GET ENTITY ======
    public AuctionEntity getEntityByAuctionId(String auctionId){

        return auctionRepository.findByAuctionId(auctionId).orElseThrow(() -> new AuctionNotFoundException("Không tìm thấy phiên đấu giá"));
    }

    // ====== HIGHEST BIDDER ======
    public String getHighestBidder(String auctionId){

        AuctionEntity auction = getEntityByAuctionId(auctionId);

        if (auction.getHighestBidder() == null){
            return null;
        }

        return auction.getHighestBidder().getUserId();
    }

    // ====== CURRENT PRICE ======
    public Money getCurrentPrice(String auctionId){
        AuctionEntity auction = getEntityByAuctionId(auctionId);

        return auction.getCurrentPrice();
    }

    // ====== SEARCH ======
    public Page<AuctionResponse> search(AuctionSearchRequest req){

        Specification<AuctionEntity> spec = AuctionSpecification.search(req);

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), Sort.by("createAt").descending());

        Page<AuctionEntity> auctions = auctionRepository.findAll(spec, pageable);

        return auctions.map(auctionMapper::toResponse);
    }
}
