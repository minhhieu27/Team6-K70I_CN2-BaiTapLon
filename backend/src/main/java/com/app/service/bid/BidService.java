package com.app.service.bid;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.app.common.money.Money;
import com.app.dto.response.bid.BidResponse;
import com.app.entity.bid.BidEntity;
import com.app.exception.auction.BidConflictException;
import com.app.mapper.BidMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor; 

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidCoreService bidCoreLogic;

    private final BidMapper bidMapper;

    private final AutoBidService autoBidService;

    // ====== ĐẤU GIÁ ======
    @Transactional
    public BidResponse placeBid(String auctionId, Money amount, String userId) {

        try{

            BidEntity bid = bidCoreLogic.excecuteBid(auctionId, amount, userId);

            autoBidService.processAutoBid(bid.getAuction());

            return bidMapper.toResponse(bid);

        } catch (ObjectOptimisticLockingFailureException e) {

            throw new BidConflictException("Không thể đấu giá cùng lúc");
        }
    }
}