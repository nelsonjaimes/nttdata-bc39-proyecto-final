package com.nttdata.bc39.grupo04.bootcoin;

import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinEntity;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@SpringBootApplication
@ComponentScan({"com.nttdata.bc39.grupo04"})
@EnableEurekaClient
public class BootcoinServiceApplication {

    @Autowired
    private MongoOperations mongoTemplate;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(BootcoinServiceApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartUp() {
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext =
                mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
        IndexOperations indexOps = mongoTemplate.indexOps(BootcoinEntity.class);
        resolver.resolveIndexFor(BootcoinEntity.class).forEach(indexOps::ensureIndex);
    }
}