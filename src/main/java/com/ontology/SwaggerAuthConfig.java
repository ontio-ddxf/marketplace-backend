package com.ontology;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

//@Configuration
//@EnableSwagger2
public class SwaggerAuthConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/v1.*";
    private final Logger log = LoggerFactory.getLogger(SwaggerAuthConfig.class);

    @Bean
    public Docket swaggerSpringfoxDocket() {

        log.debug("Starting Swagger");
        Contact contact = new Contact("Shen Yin", "https://safeoncustodian.com", "shenyin@onchain.com");

        List<VendorExtension> vext = new ArrayList<>();
        ApiInfo apiInfo = new ApiInfo("Backend API", "This is custodian API", "1.0.0", "https://baidu.com", contact, "MIT", "https://baidu.com", vext);

        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).pathMapping("/").apiInfo(ApiInfo.DEFAULT).forCodeGeneration(true).genericModelSubstitutes(ResponseEntity.class).ignoredParameterTypes(SpringDataWebProperties.Pageable.class).ignoredParameterTypes(java.sql.Date.class).directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class).directModelSubstitute(java.time.ZonedDateTime.class, Date.class).directModelSubstitute(java.time.LocalDateTime.class, Date.class).securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.newArrayList(apiKey())).useDefaultResponseMessages(false);

        docket = docket.select().paths(regex(DEFAULT_INCLUDE_PATTERN)).build();

        return docket;
    }


    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN)).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
    }
}