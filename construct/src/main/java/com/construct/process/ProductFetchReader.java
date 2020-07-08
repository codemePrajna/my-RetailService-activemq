package com.construct.process;

import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
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
    public String read() throws JSONException {
        String productName = null;
        if (productQueue.getProductStateQueue().size() == 0) {
            return null;
        }
        if (productQueue.getProductStateQueue().get(UUID.fromString(requestId)).equals(ProductEnum.COMPLETED)) {
            return null;
        }
        if (requestId == null) {
            return null;
        }
        String productId = productQueue.getProductQueue().get(UUID.fromString(requestId));
        productQueue.getProductStateQueue().put(UUID.fromString(requestId), ProductEnum.STARTED);
        try {
            ResponseEntity<String> productResponse = restTemplate.getForEntity(getTargetURL(productId), String.class);
            if (productResponse != null) {
                JSONObject jsonObject = new JSONObject(productResponse.getBody());
                if (jsonObject.getJSONObject("product").getJSONObject("item").getJSONObject("product_description") != null) {
                    JSONObject productDescription = jsonObject.getJSONObject("product").getJSONObject("item").getJSONObject("product_description");
                    productName = productDescription.getString("title");
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Product details not found");
        }
        return productName;
    }

    private String getTargetURL(String productId) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(targetUrl + productId);
        return uriComponentsBuilder.toUriString();
    }


}
