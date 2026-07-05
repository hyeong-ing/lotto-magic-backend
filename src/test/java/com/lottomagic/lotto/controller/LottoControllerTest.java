package com.lottomagic.lotto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lottomagic.lotto.dto.LottoOptionsResponse;
import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.dto.LottoResponse;
import com.lottomagic.lotto.service.LottoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LottoController.class)
class LottoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LottoService lottoService;

    @Test
    @DisplayName("GET /api/lotto/options 요청 시 선택 요소 목록을 반환한다")
    void getOptionsReturnsOptionList() throws Exception {
        // given
        List<String> options = List.of(
                "개꿈",
                "나의직감",
                "내돈",
                "행운",
                "개쩌는꿈",
                "퇴사각",
                "내집마련",
                "조상님의도움",
                "요정님도와죠",
                "엘프의선견지명",
                "한치앞이보이는내인생",
                "외계인의텔레파시",
                "내인생수직상승황금티켓",
                "다이아몬드광산주인",
                "1등이필요해",
                "제왕의자리"
        );

        when(lottoService.getOptions())
                .thenReturn(new LottoOptionsResponse(options));

        // when & then
        mockMvc.perform(get("/api/lotto/options"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.options.length()").value(16))
                .andExpect(jsonPath("$.options[0]").value("개꿈"))
                .andExpect(jsonPath("$.options[1]").value("나의직감"))
                .andExpect(jsonPath("$.options[15]").value("제왕의자리"));

        verify(lottoService).getOptions();
    }

    @Test
    @DisplayName("POST /api/lotto/draw 요청 시 로또 결과를 반환한다")
    void drawReturnsLottoResult() throws Exception {
        // given
        List<String> selectedOptions = List.of(
                "행운",
                "조상님의도움",
                "외계인의텔레파시"
        );

        LottoRequest request = new LottoRequest(selectedOptions);

        LottoResponse response = new LottoResponse(
                List.of(3, 11, 19, 27, 34, 42),
                92,
                "우주 통신 연결 완료",
                selectedOptions,
                3,
                "/3.png"
        );

        when(lottoService.draw(any(LottoRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        post("/api/lotto/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.numbers.length()").value(6))
                .andExpect(jsonPath("$.numbers[0]").value(3))
                .andExpect(jsonPath("$.numbers[1]").value(11))
                .andExpect(jsonPath("$.numbers[2]").value(19))
                .andExpect(jsonPath("$.numbers[3]").value(27))
                .andExpect(jsonPath("$.numbers[4]").value(34))
                .andExpect(jsonPath("$.numbers[5]").value(42))

                .andExpect(jsonPath("$.luckScore").value(92))
                .andExpect(jsonPath("$.luckMessage").value("우주 통신 연결 완료"))

                .andExpect(jsonPath("$.selectedOptions.length()").value(3))
                .andExpect(jsonPath("$.selectedOptions[0]").value("행운"))
                .andExpect(jsonPath("$.selectedOptions[1]").value("조상님의도움"))
                .andExpect(jsonPath("$.selectedOptions[2]").value("외계인의텔레파시"))

                .andExpect(jsonPath("$.spellNumber").value(3))
                .andExpect(jsonPath("$.spellImageUrl").value("/3.png"));

        verify(lottoService).draw(any(LottoRequest.class));
    }
}
