package com.capstoneproject.server.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author dai.le-anh
 * @since 6/11/2023
 */

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {
    private final Environment environment;


    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", environment.getProperty("cloudinary.name"),
                "api_key", environment.getProperty("cloudinary.key"),
                "api_secret", environment.getProperty("cloudinary.secret")
        ));
    }
}
