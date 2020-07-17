package com.construct.process;

import com.common.response.Response;
import com.common.util.ProductEnum;
import com.construct.util.ProductQueue;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
public class ProductFetchReaderTest {

    @InjectMocks
    ProductFetchReader productFetchReader;

    @Mock
    ProductQueue productQueue;

    @Mock
    RestTemplate restTemplate;

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testRead() throws JSONException {
        UUID randomId = UUID.randomUUID();
        productFetchReader.targetUrl = "https://redsky.target.com/v2/pdp/tcin/";
        productFetchReader.requestId = randomId.toString();
        ConcurrentHashMap productStateQueue = Mockito.mock(ConcurrentHashMap.class);
        ConcurrentHashMap productQueueMap = Mockito.mock(ConcurrentHashMap.class);

        Mockito.when(productQueue.getProductQueue()).thenReturn(productQueueMap);
        Mockito.when(productQueueMap.get(randomId)).thenReturn("12345");
        Mockito.when(productQueueMap.size()).thenReturn(1);

        Mockito.when(productQueue.getProductStateQueue()).thenReturn(productStateQueue);
        Mockito.when(productStateQueue.get(randomId)).thenReturn(ProductEnum.PENDING);
        Mockito.when(productStateQueue.size()).thenReturn(1);

        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Response<String> productresponse = Mockito.mock(Response.class);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        JSONObject jsonObject1 = Mockito.mock(JSONObject.class);
        String responseStr = "{\"product\":{\"item\":{\"product_description\":{\"title\":\"testTitle\"}}}}";
        Mockito.when(jsonObject.getJSONObject("productDescription")).thenReturn(jsonObject1);
        Mockito.when(jsonObject1.getString("title")).thenReturn("testTitle");
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), ArgumentMatchers.any(Class.class))).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(responseStr);
        Mockito.doReturn(responseEntity.getBody()).when(jsonObject).toString();
        Assert.assertEquals("testTitle", productFetchReader.read());


    }
}