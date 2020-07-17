package com.services.controller;

/*
@RunWith(SpringRunner.class)
public class ProductControllerTest {

    @Mock
    ProductServiceImpl productService;

    @InjectMocks
    ProductController productController;

    @Mock
    ProductQueue productQueue;

    Product product = new Product();

    @Before
    public void before() {

        product.setName("testName");
        product.setProductId("12345");
        product.setCurrency("USD");
        product.setPrice(10.0);
    }

    @Test
    public void testFetchProductDetails() throws InterruptedException {
        UUID randomId = UUID.randomUUID();

        Mockito.when(productService.insertProductRequest("12345")).thenReturn(randomId);
        //Mockito.when(productService.fetchProductDetails(randomId)).thenReturn(product);

        ResponseEntity<Response<Product>> response = productController.fetchProductDetails("12345");

        Assert.assertEquals(product, response.getBody().getMessage());

    }

    */
/*@Test
    public void testUpdateProductDetails() throws InterruptedException {
        UUID randomId = UUID.randomUUID();
        Mockito.when(productService.insertProductUpdateRequest(product).thenReturn(product);
        Mockito.when(productService.fetchProductDetails(randomId)).thenReturn(product);

        ResponseEntity<Response<Product>> response = productController.updateProductDetails("12345");

        Assert.assertEquals(product, response.getBody().getMessage());
    }*//*

}*/
