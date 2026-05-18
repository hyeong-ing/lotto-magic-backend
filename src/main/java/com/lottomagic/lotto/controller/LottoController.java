package com.lottomagic.lotto.controller;

import com.lottomagic.exception.ErrorResponse;
import com.lottomagic.lotto.dto.LottoOptionsResponse;
import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.dto.LottoResponse;
import com.lottomagic.lotto.service.LottoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Lotto API",
        description = "로또 번호 생성 마법진 API"
)
@RestController
@RequestMapping("/api/lotto")
public class LottoController {

    private final LottoService lottoService;

    public LottoController(LottoService lottoService) {
        this.lottoService = lottoService;
    }

    @Operation(
            summary = "선택 요소 목록 조회",
            description = "사용자가 선택할 수 있는 마법 요소 목록을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "선택 요소 목록 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LottoOptionsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/options")
    public ResponseEntity<LottoOptionsResponse> getOptions() {
        LottoOptionsResponse response = lottoService.getOptions();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로또 번호 생성",
            description = "사용자가 선택한 요소로 번호를 생성한다, 이때 요소는 반드시 3개를 선택해야한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로또 번호 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LottoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/draw")
    public ResponseEntity<LottoResponse> draw(@Valid @RequestBody LottoRequest request) {
        LottoResponse response = lottoService.draw(request);
        return ResponseEntity.ok(response);
    }
}