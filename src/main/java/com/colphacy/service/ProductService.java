package com.colphacy.service;

import com.colphacy.model.Product;
import com.colphacy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

List<Product> getAllProducts();



}
