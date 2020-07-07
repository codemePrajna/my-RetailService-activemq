package com.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//@Entity
@Data
@NoArgsConstructor
//@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "product")
public class Product {
    @Id
    //@Column(name = "productId")
            String productId;
    //@Column(name= "name")
    String name;
    //@Column(name = "currency")
    String currency;
    //@Column(name = "price")
    Double price;

    public Product(String productId, Double price, String currency) {
        this.productId = productId;
        this.price = price;
        this.currency = currency;
    }
}
