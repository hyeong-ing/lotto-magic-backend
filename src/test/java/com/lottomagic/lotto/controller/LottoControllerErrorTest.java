package com.lottomagic.lotto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lottomagic.exception.GlobalExceptionHandler;
import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.service.LottoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LottoController.class)
@Import(GlobalExceptionHandler.class)
class LottoControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LottoService lottoService;

    @Test
    @DisplayName("선택 요소가 3개가 아니면 400 에러 응답을 반환한다")
    void drawReturnsBadRequestWhenSelectedOptionsAreNotThree() throws Exception {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움")
        );

        // when & then
        mockMvc.perform(
                        post("/api/lotto/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("3개의 요소를 선택해주세요."))
                .andExpect(jsonPath("$.path").value("/api/lotto/draw"));

        verifyNoInteractions(lottoService);
    }

    @Test
    @DisplayName("존재하지 않는 선택 요소가 있으면 400 에러 응답을 반환한다")
    void drawReturnsBadRequestWhenOptionDoesNotExist() throws Exception {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움", "없는요소")
        );

        when(lottoService.draw(any(LottoRequest.class)))
                .thenThrow(new IllegalArgumentException("존재하지 않는 선택 요소가 포함되어 있습니다."));

        // when & then
        mockMvc.perform(
                        post("/api/lotto/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 선택 요소가 포함되어 있습니다."))
                .andExpect(jsonPath("$.path").value("/api/lotto/draw"));

        verify(lottoService).draw(any(LottoRequest.class));
    }

    @Test
    @DisplayName("JSON 형식이 잘못되면 400 에러 응답을 반환한다")
    void drawReturnsBadRequestWhenJsonFormatIsInvalid() throws Exception {
        // given
        String invalidJson = """
                {
                    "selectedOptions": ["행운", "조상님의도움"
                }
                """;

        // when & then
        mockMvc.perform(
                        post("/api/lotto/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("요청 JSON 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.path").value("/api/lotto/draw"));

        verifyNoInteractions(lottoService);
    }

    @Test
    @DisplayName("예상하지 못한 서버 오류가 발생하면 500 에러 응답을 반환한다")
    void drawReturnsInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움", "외계인의텔레파시")
        );

        when(lottoService.draw(any(LottoRequest.class)))
                .thenThrow(new RuntimeException("예상하지 못한 오류"));

        // when & then
        mockMvc.perform(
                        post("/api/lotto/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."))
                .andExpect(jsonPath("$.path").value("/api/lotto/draw"));

        verify(lottoService).draw(any(LottoRequest.class));
    }
}