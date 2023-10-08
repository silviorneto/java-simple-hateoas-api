package com.silvio.spring3demo.controllers;

import com.silvio.spring3demo.dtos.ProductDto;
import com.silvio.spring3demo.models.ProductModel;
import com.silvio.spring3demo.respositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> products = productRepository.findAll();
        if (!products.isEmpty()) {
//          Adicionando link (HATEOAS)
            products = products.stream().map((p) -> {
                UUID id = p.getIdProduct();
                return p.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());
            }).toList();
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable("id") UUID productId) {
        Optional<ProductModel> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        var productFound = product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product List"));
        return ResponseEntity.status(HttpStatus.OK).body(productFound);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable("id") UUID productId, @RequestBody @Valid ProductDto productDto) {
        Optional<ProductModel> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        var productToUpdate = product.get();
        BeanUtils.copyProperties(productDto, productToUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable("id") UUID productId) {
        Optional<ProductModel> productToDelete = productRepository.findById(productId);
        if (productToDelete.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepository.delete(productToDelete.get());
        return ResponseEntity.ok().body("Product deleted successfuly");
    }
}
