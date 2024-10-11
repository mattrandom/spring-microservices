package com.github.mattrandom.productservice.service;

import com.github.mattrandom.productservice.dto.ProductRequest;
import com.github.mattrandom.productservice.dto.ProductResponse;
import com.github.mattrandom.productservice.model.Product;
import com.github.mattrandom.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .withName(productRequest.name())
                .withDescription(productRequest.description())
                .withPrice(productRequest.price())
                .build();

        productRepository.save(product);

        log.info("Product [{}] has been saved.", product.getId());

        return mapToProductResponse(product);
    }

    public List<ProductResponse> getProducts() {
        List<Product> productEntities = productRepository.findAll();

        return productEntities.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
