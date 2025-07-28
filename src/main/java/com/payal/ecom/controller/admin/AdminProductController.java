package com.payal.ecom.controller.admin;

import com.payal.ecom.dto.FAQDto;
import com.payal.ecom.dto.ProductDto;

import com.payal.ecom.services.admin.adminProduct.AdminProductService;
import com.payal.ecom.services.admin.faq.FAQService;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.jaxb.SourceType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    private final FAQService faqService;

    // END POINTS FOR ADD PRODUCT
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product")
    //instead of RequestBody bcz we want to accept the multipart file which is (img) in this product dto
    public ResponseEntity<ProductDto> addProduct(@ModelAttribute ProductDto productDto) throws IOException {

        ProductDto productDto1 = adminProductService.addProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto1);

    }

    // END POINT

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        List<ProductDto> productDtos = adminProductService.getAllProducts();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductDto>> getAllProductByName(@PathVariable String name) {

        List<ProductDto> productDtos = adminProductService.getAllProductsByName(name);
        return ResponseEntity.ok(productDtos);
    }


    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        boolean deleted = adminProductService.deleteProduct(productId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/faq/{productId}")
    public ResponseEntity<FAQDto> postFAQ(@PathVariable Long productId, @RequestBody FAQDto faqDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(faqService.postFAQ(productId, faqDto));
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {

        ProductDto productDto = adminProductService.getProductById(productId);

        if(productDto != null){
            return ResponseEntity.ok(productDto);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId, @ModelAttribute ProductDto productDto) throws IOException {
        ProductDto updatedProduct = adminProductService.updateProduct(productId, productDto);
        if(updatedProduct != null){
            return ResponseEntity.ok(updatedProduct);
        }else{
            return ResponseEntity.notFound().build();
        }
    }





}
