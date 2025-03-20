package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
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
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> result = repository.findAll(pageable);
        Page<CategoryDTO> dto = result.map(CategoryDTO::new);
        return dto;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category cat = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found!"));
        return new CategoryDTO(cat);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category cat = new Category();
        cat.setName(dto.getName());
        cat = repository.save(cat);
        return new CategoryDTO(cat);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try {
            Category cat = repository.getReferenceById(id);
            cat.setName(dto.getName());
            cat = repository.save(cat);
            return new CategoryDTO(cat);
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

}
