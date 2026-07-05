package com.lottomagic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "service", "lotto-magic-backend",
                "status", "running",
                "message", "로또 마법진 백엔드 서버가 실행 중입니다."
        );
    }
    
}
