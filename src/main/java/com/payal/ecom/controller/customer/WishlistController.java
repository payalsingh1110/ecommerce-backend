package com.payal.ecom.controller.customer;

import com.payal.ecom.dto.WishlistDto;
import com.payal.ecom.services.customer.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/wishlist")
    public ResponseEntity<?> addProductToWishlist(@RequestBody WishlistDto wishlistDto){
        WishlistDto postedWishlistDto = wishlistService.addProductToWishlist(wishlistDto);
        if(postedWishlistDto == null ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(postedWishlistDto);
    }

    @GetMapping("/wishlist/{userId}")
    public ResponseEntity<List<WishlistDto>> getWishlistByUserId(@PathVariable Long userId){
        log.info("userId: {}",userId);
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }


}
