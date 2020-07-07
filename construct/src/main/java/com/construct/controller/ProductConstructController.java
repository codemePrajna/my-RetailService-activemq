package com.construct.controller;

import com.common.response.Response;
import com.construct.service.ProductConstructService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductConstructController {

    @Autowired
    ProductConstructService productConstructService;

    /**
     * function to construct product details
     *
     * @return
     */
    @ApiOperation(value = "Load Product details to product database")
    @GetMapping(value = "/product/load", produces = "application/json")
    public ResponseEntity<Response<String>> loadProductDetails() {
        productConstructService.loadProductDetails();
        return new Response<String>()
                .setStatus(200)
                .setMessage("Product Loaded successfully").toResponseEntity();
    }
}
