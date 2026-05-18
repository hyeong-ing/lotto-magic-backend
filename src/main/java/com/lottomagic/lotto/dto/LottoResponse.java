package com.lottomagic.lotto.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "로또 번호 생성 응답")
public record LottoResponse (
        @Schema(
                description = "생성된 로또 번호는 6개이다.",
                example = "[3, 11, 19, 27, 34, 42]"
        )
        List<Integer> numbers,

        @Schema(
                description = "오늘의 행운 점수",
                example = "84"
        )
        int luckScore,

        @Schema(
                description = "행운의 메시지",
                example = "우주 통신 연결 완료"
        )
        String luckMessage,

        @Schema(
                description = "사용자가 선택한 요소 목록",
                example = "[\"행운\", \"조상님의도움\", \"외계인의텔레파시\"]"
        )
        List<String> selectedOptions,

        @Schema(
                description = "선택한 마법진 번호",
                example = "3"
        )
        int spellNumber,

        @Schema(
                description = "마법진 이미지 경로",
                example = "/images/spells/3.png"
        )
        String spellImageUrl
){
}
