package com.services.service.impl;

import com.common.entity.Product;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
public class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    ProductQueue productQueue;

    Product product = new Product();

    @Before
    public void setUp() throws Exception {
        product.setName("testName");
        product.setProductId("12345");
        product.setCurrency("USD");
        product.setPrice(10.0);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFetchProductDetails() throws InterruptedException {
        Product product1 = new Product("12345", 10.0, "USD");
        product.setName("testName");
        product.setProductId("12345");
        product.setCurrency("USD");
        product.setPrice(10.0);
        UUID randomId = UUID.randomUUID();
        ConcurrentHashMap productStateQueue = Mockito.mock(ConcurrentHashMap.class);

        Product product = Mockito.mock(Product.class);
        Mockito.when(productQueue.getProductStateQueue()).thenReturn(productStateQueue);
        Mockito.when(productStateQueue.get(randomId)).thenReturn(ProductEnum.PENDING);
        //Mockito.doReturn(productQueue.productStateQueue).when(testMap).entrySet();

        Mockito.doReturn(productRepository.findByProductId("12345")).when(product).toString();

        Product productResponse = productService.fetchProductDetails(randomId);
        Assert.assertEquals(product, productResponse);
    }

    public void testUpdateProduct() {
    }

    public void testInsertProductRequest() {
    }

    public void testInsertProductUpdateRequest() {
    }
}