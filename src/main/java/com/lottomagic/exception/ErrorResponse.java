package com.lottomagic.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "API 에러 응답")
public record ErrorResponse (

    @Schema(
            description = "HTTP 상태 코드",
            example = "400"
    )
    int status,

    @Schema(
            description = "HTTP 에러 이름",
            example = "Bad Request"
    )
    String error,

    @Schema(
            description = "에러 메시지",
            example = "3개의 요소를 선택해주세요."
    )
    String message,

    @Schema(
            description = "요청 경로",
            example = "/api/lotto/draw"
    )
    String path,

    @Schema(
            description = "에러 발생 시간",
            example = "2026-05-17T15:30:12.123"
    )
    LocalDateTime timestamp

) {

}
