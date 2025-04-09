package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO dto;
    private Category category;
    private CategoryDTO catDTO;

    @BeforeEach
    void setUp() {
        // Esses valores a seguir não seguem o que está no seed do DB.
        //São valores mockados, utilizados para os testes.
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 2L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        dto = Factory.createProductDTO();
        category = Factory.createCategory();
        catDTO = Factory.createCategoryDTO();

        //Configurando os comportamentos simulados

        //Comp. FindAll w/ Page<>
        when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        //Comp. FindById
        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Comp. Save - Insert and Update.
        when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Comp. Update
        when(categoryRepository.getReferenceById(catDTO.getId())).thenReturn(category);
        when(repository.getReferenceById(existingId)).thenReturn(product);
        when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        //Comps. Delete
        when(repository.existsById(existingId)).thenReturn(true);
        when(repository.existsById(nonExistingId)).thenReturn(false);
        when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

    }

    //FindAll w/ Page
    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, times(1)).findAll(pageable);
    }

    // FindById
    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
    }
    @Test
    public void findByIdShouldThrowExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           ProductDTO result = service.findById(nonExistingId);
        });
    }

    //UPDATE
    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
    }
    @Test
    public void updateShouldAReturnResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           service.update(nonExistingId, dto);
        });
    }

    // DELETE
    @Test
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

        // Posso usar assim também:
        // retirando o Mockito, assim ele importa as classes diretamente nos imports.

        //verify(repository, times(1)).deleteById(existingId);
    }
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           service.delete(nonExistingId);
        });
    }
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){

        Assertions.assertThrows(DataBaseException.class, () -> {
           service.delete(dependentId);
        });
    }
}
