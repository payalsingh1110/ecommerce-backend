package com.payal.ecom.services.admin.category;

import com.payal.ecom.dto.CategoryDto;
import com.payal.ecom.entity.Category;
import com.payal.ecom.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    public Category createCategory(CategoryDto categoryDto){
        Category category = new Category();
        category.setName(categoryDto.getName());

        //  Fixed: description was being set incorrectly before
        category.setDescription(categoryDto.getDescription());

        return categoryRepo.save(category);
    }

    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }

}
