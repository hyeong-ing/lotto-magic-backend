package com.lottomagic.lotto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "로또 번호 생성 요청")
public record LottoRequest (
    @Schema(
            description = "사용자가 선택한 마법 요소 목록으로 3개를 선택애햐 한다.",
            example = "[\"행운\", \"조상님의도움\", \"외계인의텔레파시\"]"
    )
    @NotNull(message = "선택 요소는 필수입니다.")
    @Size(min = 3, max = 3, message = "3개의 요소를 선택해주세요.")
    List<@NotBlank(message = "요소를 선택해주세요.") String> selectedOptions
) {
}
