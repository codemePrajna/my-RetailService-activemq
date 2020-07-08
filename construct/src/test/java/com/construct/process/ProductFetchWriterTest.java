package com.construct.process;

import com.common.entity.Product;
import com.common.repository.ProductRepository;
import com.common.util.ProductQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
public class ProductFetchWriterTest {

    @Mock
    ProductQueue productQueue;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductFetchWriter productFetchWriter;

    Product product = new Product();

    @Before
    public void setUp() throws Exception {
        product.setName("testName");
        product.setProductId("12345");
        product.setCurrency("USD");
        product.setPrice(10.0);
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testWrite() throws Exception {
        UUID randomId = UUID.randomUUID();
        productFetchWriter.requestId = randomId.toString();

        ConcurrentHashMap productQueueMap = Mockito.mock(ConcurrentHashMap.class);
        Mockito.when(productQueue.getProductQueue()).thenReturn(productQueueMap);
        Mockito.when(productQueueMap.get(randomId)).thenReturn("12345");
        Mockito.when(productQueueMap.size()).thenReturn(1);

        Product productInput = Mockito.mock(Product.class);
        Mockito.when(productRepository.findByProductId(Mockito.anyString())).thenReturn(product);
        //Mockito.doReturn(product).when(productRepository).findByProductId(Mockito.anyString());

        Mockito.when(productInput.getName()).thenReturn("testName");
        Mockito.doReturn(product).when(productRepository).save(Mockito.any(Product.class));

        ConcurrentHashMap productStateQueue = Mockito.mock(ConcurrentHashMap.class);
        Mockito.when(productQueue.getProductStateQueue()).thenReturn(productStateQueue);

        productFetchWriter.write(Mockito.anyList());
        Assert.assertEquals(productRepository.findByProductId("12345"), product);


    }
}