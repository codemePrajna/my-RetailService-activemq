package com.services.controller;


import com.common.entity.Product;
import com.common.response.Response;
import com.common.util.SharedObject;
import com.services.service.ProductResponse;
import com.services.service.impl.ProductServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
public class ProductControllerTest {

    @Mock
    ProductServiceImpl productService;

    @InjectMocks
    ProductController productController;


    Product product = new Product();

    @Before
    public void before() {

        product.setName("testName");
        product.setProductId("12345");
        product.setCurrency("USD");
        product.setPrice(10.0);
    }

    @Test
    public void testFetchProductDetails() throws Exception {
        UUID randomId = UUID.randomUUID();
        ConcurrentHashMap productResponseMap = Mockito.mock(ConcurrentHashMap.class);
        ProductResponse productResponse = Mockito.mock(ProductResponse.class);
        Mockito.when(productResponse.getProductResponse()).thenReturn(productResponseMap);
        Mockito.when(productResponseMap.get(randomId)).thenReturn(product);
        SharedObject sharedObject = Mockito.mock(SharedObject.class);
        //Mockito.when(SharedObject.sharedObj.wait()).thenReturn(sharedObject.notify());
        Mockito.when(productService.insertProductRequest("12345")).thenReturn(randomId);
        Mockito.when(productResponse.getProductResponse().get(randomId)).thenReturn(product);
        synchronized (SharedObject.sharedObj) {
            SharedObject.sharedObj.notify();
        }

        ResponseEntity<Response<Product>> response = productController.fetchProductDetails("12345");

        Assert.assertEquals(product, response.getBody().getMessage());

    }
}


/*@Test
    public void testUpdateProductDetails() throws InterruptedException {
        UUID randomId = UUID.randomUUID();
        Mockito.when(productService.insertProductUpdateRequest(product).thenReturn(product);
        Mockito.when(productService.fetchProductDetails(randomId)).thenReturn(product);

        ResponseEntity<Response<Product>> response = productController.updateProductDetails("12345");

        Assert.assertEquals(product, response.getBody().getMessage());
    }

}*/
