package com.app.service.bid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.dto.response.bid.BidResponse;
import com.app.mapper.BidMapper;
import com.app.repository.BidRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BidQuerryService {

    private final BidRepository bidRepository;

    private final BidMapper bidMapper;

    public Page<BidResponse> getAuctionBidHistory(String auctionId, int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createBidAt").descending());

        return bidRepository.findByAuction_AuctionIdOrderByCreateBidAtDesc(auctionId, pageable).map(bidMapper::toResponse);
    }
}
