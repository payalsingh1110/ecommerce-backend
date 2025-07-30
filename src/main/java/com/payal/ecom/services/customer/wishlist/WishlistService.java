package com.payal.ecom.services.customer.wishlist;

import com.payal.ecom.dto.WishlistDto;

import java.util.List;

public interface WishlistService {
    WishlistDto addProductToWishlist(WishlistDto wishlistDto);

    List<WishlistDto> getWishlistByUserId(Long userId);

   //void removeFromWishlist(Long userId, Long productId);
}
