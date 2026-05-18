package com.lottomagic.lotto.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "로또 선택 요소 목록")
public record LottoOptionsResponse (
    @Schema(
            description = "사용자가 선택하는 마법 요소 목록",
            example = "[\"개꿈\", \"나의직감\", \"내돈\", \"행운\"]"
    )
    List<String> options
){
}
