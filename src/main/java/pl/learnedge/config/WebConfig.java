package pl.learnedge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/profile-pictures}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Map any request under /uploads/** to the local ./uploads/ folder
        // Example: GET /uploads/profile-pictures/abc.jpg -> ./uploads/profile-pictures/abc.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}