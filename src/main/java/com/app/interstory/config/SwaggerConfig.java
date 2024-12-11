package com.app.interstory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("EST SOFT BE[4] TEAM PROJECT #4조_'InterStory'") // API 제목
                .description("4조의 project_InterStory API 문서.") // API 설명
                .version("1.0.0"); // 프로젝트 배포할 때 최초 API 버전
    }

}
