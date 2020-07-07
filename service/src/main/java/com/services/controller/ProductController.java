package com.services.controller;

import com.common.entity.Product;
import com.common.response.Response;
import com.services.service.impl.ProductServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/v1/product")
public class ProductController {
    @Autowired
    ProductServiceImpl productService;

    /**
     * Function to fetch the product details
     *
     * @param productId
     * @return
     * @throws InterruptedException
     */
    @ApiOperation(value = "Fetch product Details for a given Product")
    @GetMapping(value = "/{productId}", produces = "application/json")
    public ResponseEntity<Response<Product>> fetchProductDetails(@ApiParam(value = "ProductId", required = true) @PathVariable String productId) throws InterruptedException {
        UUID productReqId = productService.insertProductRequest(productId);
        Product product = productService.fetchProductDetails(productReqId);
        return new Response<Product>()
                .setStatus(200)
                .setMessage(product).toResponseEntity();
    }

    /**
     * function to update the product details
     *
     * @param productId
     * @return
     * @throws InterruptedException
     */
    @ApiOperation(value = "Update product price details for a given Product")
    @PutMapping(value = "/{productId}", produces = "application/json")
    public ResponseEntity<Response<Product>> updateProductDetails(@ApiParam(value = "ProductId", required = true) @PathVariable String productId) throws InterruptedException {
        UUID productReqId = productService.insertProductUpdateRequest(productId);
        Product product = productService.fetchProductDetails(productReqId);
        return new Response<Product>()
                .setStatus(200)
                .setMessage(product).toResponseEntity();
    }
}


