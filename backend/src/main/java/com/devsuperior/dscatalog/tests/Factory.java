package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(1L, "Phone", "Good phone", 800.0, "https://img.com/img.png");
        product.getCategories().add(createCategory());
        return product;
    }

    public static Category createCategory(){
        Category category = new Category(2L, "Electronics");
        return category;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product);
    }

    public static CategoryDTO createCategoryDTO(){
        Category category = createCategory();
        return new CategoryDTO(category);
    }
}
