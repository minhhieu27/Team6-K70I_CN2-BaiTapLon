package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.common.enums.ItemType;
import com.app.common.enums.Role;
import com.app.common.enums.UserStatus;
import com.app.dto.request.auction.CreateAuctionRequest;
import com.app.dto.request.item.CreateElectronicsAuctionRequest;
import com.app.entity.user.UserEntity;
import com.app.repository.UserRepository;
import com.app.service.auction.AuctionManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartRunner implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuctionManagementService auctionManagementService;

    @Override
    public void run(String...args){
        boolean adminExists = userRepository.existsByUsername("admin");
        if (adminExists) return;

        UserEntity admin = new UserEntity("admin", "admin@app.com", "0999999999", passwordEncoder.encode("Admin@123"));
        admin.setStatus(UserStatus.ACTIVE);
        admin.addRole(Role.ROLE_ADMIN);
        admin.addRole(Role.ROLE_SELLER); 
        userRepository.save(admin);
        log.info("Default admin created");

        UserEntity savedAdmin = userRepository.findByUsername("admin").get();

        String[] phones = {"iPhone 15 Pro Max", "Samsung Galaxy S24 Ultra", "MacBook Pro M3", "iPad Pro 2024", "Apple Watch Ultra", "AirPods Max", "Sony PS5", "Nintendo Switch", "Bàn phím cơ Keychron", "Chuột Logitech MX Master"};
        long[] prices = {25000000, 22000000, 45000000, 30000000, 15000000, 12000000, 14000000, 8000000, 3500000, 2500000};
        String[] images = {
            "https://images.unsplash.com/photo-1696446701796-da61225697cc?w=500",
            "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500",
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500",
            "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500",
            "https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?w=500",
            "https://images.unsplash.com/photo-1613040809024-b4def773ac27?w=500",
            "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=500",
            "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=500",
            "https://images.unsplash.com/photo-1595225476474-87563907a212?w=500",
            "https://images.unsplash.com/photo-1527814050087-37938154791f?w=500"
        };

        for(int i=0; i<10; i++) {
            CreateElectronicsAuctionRequest item = new CreateElectronicsAuctionRequest();
            // ĐÃ THÊM DÒNG FIX LỖI Ở ĐÂY:
            item.setItemType(ItemType.ELECTRONICS);
            
            item.setItemName(phones[i]); item.setDescription("Hàng chính hãng, bảo hành 12 tháng."); item.setStartPrice(BigDecimal.valueOf(prices[i]));
            item.setBrand("Hãng " + i); item.setModel("Model " + i); item.setConditionStatus("Mới 100%"); item.setColor("Đen"); item.setStorage("256GB"); item.setWarrantyMonths(12);
            
            CreateAuctionRequest req = new CreateAuctionRequest();
            req.setTitle("Đấu giá siêu phẩm: " + phones[i]); req.setItem(item); req.setImageUrls(List.of(images[i]));
            
            auctionManagementService.createAuction(req, savedAdmin.getUserId());
        }
        log.info("Đã tạo tự động 10 sản phẩm lên Sàn Đấu Giá!");
    }
}