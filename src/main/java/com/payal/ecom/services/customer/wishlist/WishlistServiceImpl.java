package com.payal.ecom.services.customer.wishlist;

import com.payal.ecom.dto.WishlistDto;
import com.payal.ecom.entity.Product;
import com.payal.ecom.entity.User;
import com.payal.ecom.entity.Wishlist;
import com.payal.ecom.repository.ProductRepository;
import com.payal.ecom.repository.UserRepository;
import com.payal.ecom.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService{

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final WishlistRepository wishlistRepository;

    public WishlistDto addProductToWishlist(WishlistDto wishlistDto){
        Optional<Product> optionalProduct = productRepository.findById(wishlistDto.getProductId());
        Optional<User> optionalUser = userRepository.findById(wishlistDto.getUserId());

        if(optionalProduct.isPresent() && optionalUser.isPresent()){
            Wishlist wishlist = new Wishlist();
            wishlist.setProduct(optionalProduct.get());
            wishlist.setUser(optionalUser.get());

            return  wishlistRepository.save(wishlist).getWishlistDto();
        }
        return null;
    }

    public List<WishlistDto> getWishlistByUserId(Long userId){
        return wishlistRepository.findAllByUserId(userId)
                .stream()
                .map(Wishlist::getWishlistDto)
                .collect(Collectors.toList());
    }
//
//    public void removeFromWishlist(Long userId, Long productId) {
//        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);
//        if (wishlist != null) {
//            wishlistRepository.delete(wishlist);
//        } else {
//            throw new RuntimeException("Wishlist item not found");
//        }
//    }

}
