package com.construct.process;

import com.common.model.ProductRequest;
import com.common.util.ProductEnum;
import com.common.util.TrackTimeUtil;
import com.construct.util.ProductQueue;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Reader class to fetch product name from target url for a given product id
 */
@Slf4j
@Component
@StepScope
public class ProductFetchReader implements ItemReader<String> {
    @Value("#{jobParameters['requestId']}")
    protected String requestId;
    @Value("${target.product.url}")
    String targetUrl;
    @Autowired
    ProductQueue productQueue;
    @Autowired
    RestTemplate restTemplate;


    @Override
    @TrackTimeUtil
    public String read() throws JSONException {

        String productName = null;
        //return from reader if no request found
        if (productQueue.getProductQueue().size() == 0) {
            return null;
        }
        //return from reader if map has no request enqueued
        if (productQueue.getProductQueue().get(UUID.fromString(requestId)) == null) {
            return null;
        }
        //return from reader if the request has been fetched and processed
        ProductRequest productRequest = productQueue.getProductQueue().get(UUID.fromString(requestId));
        if (productRequest.getProductState().equals(ProductEnum.COMPLETED)) {
            return null;
        }
        String productId = productRequest.getProductId();
        productQueue.getProductQueue().get(UUID.fromString(requestId)).setProductState(ProductEnum.STARTED);
        try {
            //get the title information from target url
            ResponseEntity<String> productResponse = restTemplate.getForEntity(getTargetURL(productId), String.class);
            if (productResponse != null) {
                JSONObject jsonObject = new JSONObject(productResponse.getBody());
                if (jsonObject.getJSONObject("product").getJSONObject("item").getJSONObject("product_description") != null) {
                    JSONObject productDescription = jsonObject.getJSONObject("product").getJSONObject("item").getJSONObject("product_description");
                    productName = productDescription.getString("title");
                }

            }
        } catch (Exception e) {
            productQueue.getProductQueue().get(UUID.fromString(requestId)).setProductState(ProductEnum.FAILED);
            throw new RuntimeException("Product details not found");
        }
        return productName;
    }

    private String getTargetURL(String productId) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(targetUrl + productId);
        return uriComponentsBuilder.toUriString();
    }


}
