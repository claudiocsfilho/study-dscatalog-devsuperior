package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> result = repository.findAll(pageable);
        Page<ProductDTO> dto = result.map(ProductDTO::new);
        return dto;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product prod = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found!"));
        return new ProductDTO(prod);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product prod = new Product();
        copyDtoToEntity(dto, prod);
        prod = repository.save(prod);
        return new ProductDTO(prod);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {
            Product prod = repository.getReferenceById(id);
            copyDtoToEntity(dto, prod);
            prod = repository.save(prod);
            return new ProductDTO(prod);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found!" + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Id not found");
        } try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new DataBaseException("Referential integrity failure");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product prod) {
        prod.setName(dto.getName());
        prod.setDescription(dto.getDescription());
        prod.setPrice(dto.getPrice());
        prod.setImgUrl(dto.getImgUrl());

        //Limpo a lista e no FOR insiro uma nova
        prod.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()){
            Category cat = new Category();
            cat.setId(catDto.getId());
            cat.setName(catDto.getName());
            prod.getCategories().add(cat);
        }
    }
}
