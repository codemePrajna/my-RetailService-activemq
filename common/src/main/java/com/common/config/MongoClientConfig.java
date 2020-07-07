package com.common.config;

/*
@Configuration
@EnableMongoRepositories(basePackages = "com.common.repository")
public class MongoClientConfig extends AbstractMongoConfiguration {
    @Override
    public String getDatabaseName() {
        return "retailDb";
    }
    @Override
    @Bean
    public MongoClient mongoClient() {
        ServerAddress address = new ServerAddress("localhost", 27017);
        MongoCredential credential = MongoCredential.createCredential("prajnah", getDatabaseName(), "cp".toCharArray());
        MongoClientOptions options = new MongoClientOptions.Builder().build();

        MongoClient client = new MongoClient(address, credential, options);
        return client;
    }
}*/
