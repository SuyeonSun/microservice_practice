package com.programmingtechie.product.service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.programmingtechie.product.service.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
