package com;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Created by 半仙.
 */
@Configuration
public class RedisLimiterConfiguration {

    // HostAddress的Key
    //TODO:像KeyResolver 会有多个，比如商品模块，用户模块，下单模块，以不同的KeyResolver来定义，之所以用  @Primary
    //TODO:是因为要getway网关默认用这个KeyResolver。如果配置了两个KeyResolver，用法看RoutesConfiguration
    @Bean
    @Primary
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    @Bean("redisLimiterUser")
    @Primary
    public RedisRateLimiter redisLimiterUser() {
        return new RedisRateLimiter(10, 20);
    }

    @Bean("redisLimiterItem")
    public RedisRateLimiter redisLimiterItem() {
        return new RedisRateLimiter(20, 50);
    }

    // TODO 尝试实现一个in-memory限流器

}
