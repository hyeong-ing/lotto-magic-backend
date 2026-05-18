package com.lottomagic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI lottoMagicOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버");

        return new OpenAPI()
                .servers(List.of(localServer))
                .info(new Info()
                        .title("로또 번호 생성 마법진 API")
                        .description("""
                                로또 번호 생성 마법진 프로젝트의 백엔드 API 문서입니다.
                                
                                주요 기능:
                                - 선택 가능한 마법 요소 목록 조회
                                - 선택 요소 3개를 기반으로 로또 번호 생성
                                - 행운 지수, 행운 메시지, 주문진 이미지 정보 반환
                                - 공통 에러 응답 처리
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lotto Magic")
                                .email("oddcoding64@gmail.com")
                        )
                );
    }
}