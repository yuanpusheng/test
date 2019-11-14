package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }


    @Cacheable(cacheNames = "redisCache")
    public void getCache(String key,Integer expire,String name){

    }


}
