# My Retail Service

My Retail Service provides set of API endpoints to fetch and update product details

## Use Case:
myRetail RESTful service (Target case Study POC)

myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps. The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller. Your goal is to create a RESTful service that can retrieve product and price details by ID. The URL structure is up to you to define, but try to follow some sort of logical convention. Build an application that performs the following actions:

Responds to an HTTP GET request at /products/{id} and delivers product data as JSON (where {id} will be a number.
Example product IDs: 15117729, 16483589, 16696652, 16752456, 15643793)
Example response: {"id":13860428,"name":"The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value": 13.49,"currency_code":"USD"}}
Performs an HTTP GET to retrieve the product name from an external API. (For this exercise the data will come from redsky.target.com, but let’s just pretend this is an internal resource hosted by myRetail)
Example: http://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics
Reads pricing information from a NoSQL data store and combines it with the product id and name from the HTTP request into a single response.
BONUS: Accepts an HTTP PUT request at the same path (/products/{id}), containing a JSON request body similar to the GET response, and updates the product’s price in the data store

## Tech Stack

- Oracle open JDK 14
- Maven 3.8.1
- ActiveMq 5.14
- Junit 3
- Spring boot 2.2.5 -multimodule
- RestTemplate
- Embedded Mongo DB 3.2.2
- Swagger 2

## Modules and description
1. myRetail - main application that wraps all the modules
2. common - holds common library for rest of the modules
3. service - REST layer to serve the user request and publish in the queue
4. construct - backend module that looks for the queue and constructs product information and loads/updates the database

## Design description
- Implements producer-> consumer approach
- User requests for product details with product ID
- Service assigns a unique identifier to each request and inserts into queue
- service waits for the product to be loaded in database
- construct consumes the request and consolidates the info with target url and loads the database
- constuct notifies the service when loaded or error-ed
- service serves the requests to the user

## Technology used
- Spring Batch
- Spring scheduler
- Multithreading concepts
- producer-consumer
- stateless communications
- Oauth 2 security
- Swagger

## Project Status
- Initial working cut from receiving the data from user to publishing it back with details
- Unit test for few scenarios only

## RoadMap

- Caching
- Pagination
- Resiliency
- kafka /Blocking Queue wrapped on hashmap
- DB sharding and scaling

## To run the application
Download the project. Navigate to the myRetail project folder and run mvn spring-boot:run (RetailServiceApplication) Read the API documentation in Swagger2 (run http://localhost:8080/swagger-ui.html on the browser)

## Screenshots
![Alt text](.\images\LoadProduct.jpg)
![Fetch Product](.\images\FetchProduct.jpg)
![Update Product](.\images\UpdateProduct.jpg)
