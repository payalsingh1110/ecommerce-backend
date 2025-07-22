package com.payal.ecom.services.admin.category;

import com.payal.ecom.dto.CategoryDto;
import com.payal.ecom.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDto categoryDto);

    List<Category> getAllCategories();
}
