package com.services.controller;

import com.common.entity.Product;
import com.common.response.Response;
import com.common.util.SharedObject;
import com.services.service.ProductResponse;
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
@RequestMapping("/api/v1/product")
public class ProductController {
    @Autowired
    ProductServiceImpl productService;

    @Autowired
    ProductResponse productResponse;


    /**
     * Function to fetch the product details
     *
     * @param productId
     * @return
     * @throws InterruptedException
     */
    @ApiOperation(value = "Fetch product Details for a given Product")
    @GetMapping(value = "/{productId}", produces = "application/json")
    public ResponseEntity<Response<Product>> fetchProductDetails(@ApiParam(value = "ProductId", required = true) @PathVariable String productId) throws InterruptedException,Exception {
        UUID productReqId = productService.insertProductRequest(productId);
        //wait for the response from construct to load the product
        synchronized (SharedObject.sharedObj) {
            SharedObject.sharedObj.wait();
        }
        Product product = productResponse.getProductResponse().get(productReqId);
        if(product == null)
        throw new RuntimeException("Product Details not available for: "+ productId);
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
    public ResponseEntity<Response<Product>> updateProductDetails(@ApiParam(value = "ProductId", required = true) @PathVariable String productId
            , @RequestBody Product productInput) throws InterruptedException {
        productInput.setProductId(productId);
        UUID productReqId = productService.insertProductUpdateRequest(productInput);
        //wait for the response from construct to load the product
        synchronized (SharedObject.sharedObj) {
            SharedObject.sharedObj.wait();
        }
        Product product = productResponse.getProductResponse().get(productReqId);
        if(product == null)
            throw new RuntimeException("Product Details not available for: "+ productId);
        return new Response<Product>()
                .setStatus(200)
                .setMessage(product).toResponseEntity();
    }
}


