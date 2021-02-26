package com.user;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 半仙.
 */
@Configuration
@RefreshScope
@Data
public class UserApplicationProperties {


    //todo:这个@value是从github上取下来的
    @Value("${userservice.registration.disabled}")
    private boolean disableRegistration;

}
