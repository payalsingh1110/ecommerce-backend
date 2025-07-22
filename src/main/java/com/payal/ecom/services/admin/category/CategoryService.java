package com.payal.ecom.services.admin.category;

import com.payal.ecom.dto.CategoryDto;
import com.payal.ecom.entity.Category;

public interface CategoryService {

    Category createCategory(CategoryDto categoryDto);
}
