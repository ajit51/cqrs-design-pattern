package com.cqrs.product_command_service.service;

import com.cqrs.product_command_service.dto.ProductEvent;
import com.cqrs.product_command_service.entity.Product;
import com.cqrs.product_command_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Product createProduct(ProductEvent productEvent) {
        Product productDo = productRepository.save(productEvent.getProduct());
        ProductEvent event = new ProductEvent("CreateProduct", productDo);
        kafkaTemplate.send("product-event-topic", event);
        return productDo;
    }

    public Product updateProduct(long id, ProductEvent productEvent) {
        Product existingProduct = productRepository.findById(id).get();
        Product newProduct = productEvent.getProduct();

        existingProduct.setName(newProduct.getName());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setDescription(newProduct.getDescription());
        Product productDO = productRepository.save(existingProduct);

        ProductEvent event = new ProductEvent("UpdateProduct", productDO);
        kafkaTemplate.send("product-event-topic", event);
        return productDO;
    }
}
