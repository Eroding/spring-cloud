package com.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by 半仙.
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.imooc.item.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
@EnableDiscoveryClient
// TODO feign注解,因为相对独立，所以不需要引入feign组件，但是像order这种就会引入组件
public class ItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }

}
