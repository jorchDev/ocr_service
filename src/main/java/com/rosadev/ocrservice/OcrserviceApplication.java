package com.rosadev.ocrservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rosadev.ocrservice.lib.FileHandler;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@EnableSwagger2
public class OcrserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcrserviceApplication.class, args);
	}
	
	@Value("${ocr.filesPath:./}")
    String filesPath;

	@Bean
	public Docket api() {

		List<SecurityScheme> securitySchemes = new ArrayList<SecurityScheme>();
		securitySchemes.add(apiKey());
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.rosadev.ocrservice.controller")).paths(PathSelectors.any())
				.build().apiInfo(apiInfo())
				.securitySchemes(securitySchemes);
    }
    
	ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("API OCR").description("Servicio obtención de texto en imagenes(OCR)")
				.version("1.0.0").contact(new Contact("", "", "jorch.dev@gmail.com")).build();
	}

	private ApiKey apiKey() {    
		return new ApiKey("apiKey", "Authorization", "header"); 
	}

	@Bean
    public FileHandler getFileHandler()
    {
        return new FileHandler(filesPath);
    }


}
