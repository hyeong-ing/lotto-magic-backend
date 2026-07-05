package com.lottomagic.lotto.service;

import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.dto.LottoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LottoServiceTest {

    private final LottoService lottoService = new LottoService();

    @Test
    @DisplayName("선택 요소 3개를 보내면 로또 결과가 정상 생성된다")
    void passLottoResult() {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움", "외계인의텔레파시")
        );

        // when
        LottoResponse response = lottoService.draw(request);

        // then
        assertNotNull(response);

        assertAll(
                () -> assertEquals(6, response.numbers().size()),
                () -> assertLottoNumber(response.numbers()),
                () -> assertNumberUnique(response.numbers()),
                () -> assertNumberSort(response.numbers()),

                () -> assertTrue(response.luckScore() >= 0),
                () -> assertTrue(response.luckScore() <= 100),
                () -> assertEquals(luckMessage(response.luckScore()), response.luckMessage()),

                () -> assertEquals(request.selectedOptions(), response.selectedOptions()),

                () -> assertTrue(response.spellNumber() >= 1),
                () -> assertTrue(response.spellNumber() <= 9),
                () -> assertEquals(
                        "/" + response.spellNumber() + ".png",
                        response.spellImageUrl()
                )
        );
    }

    @Test
    @DisplayName("선택 요소가 2개면 예외가 발생한다")
    void exceptionTwo() {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움")
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> lottoService.draw(request)
        );

        // then
        assertEquals("요소는 정확히 3개 선택해야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("선택 요소가 4개면 예외가 발생한다")
    void exceptionFour() {
        // given
        LottoRequest request = new LottoRequest(
                List.of("행운", "조상님의도움", "외계인의텔레파시", "내돈")
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> lottoService.draw(request)
        );

        // then
        assertEquals("요소는 정확히 3개 선택해야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("선택 요소가 null이면 예외가 발생한다")
    void exceptionNull() {
        // given
        LottoRequest request = new LottoRequest(null);

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> lottoService.draw(request)
        );

        // then
        assertEquals("요소는 정확히 3개 선택해야 합니다.", exception.getMessage());
    }


    private void assertLottoNumber(List<Integer> numbers) {
        for (int number : numbers) {
            assertTrue(number >= 1, "로또 번호는 1 이상이어야 합니다.");
            assertTrue(number <= 45, "로또 번호는 45 이하여야 합니다.");
        }
    }

    private void assertNumberUnique(List<Integer> numbers) {
        Set<Integer> uniqueNumbers = new HashSet<>(numbers);

        assertEquals(
                numbers.size(),
                uniqueNumbers.size(),
                "로또 번호는 중복될 수 없습니다."
        );
    }

    private void assertNumberSort(List<Integer> numbers) {
        for (int i = 0; i < numbers.size() - 1; i++) {
            assertTrue(
                    numbers.get(i) < numbers.get(i + 1),
                    "로또 번호는 오름차순이어야 합니다."
            );
        }
    }

    private String luckMessage(int luckScore) {
        if (luckScore <= 20) {
            return "우주 와이파이 끊김";
        }

        if (luckScore <= 40) {
            return "조상님이 애쓰는 중";
        }

        if (luckScore <= 60) {
            return "요정의 행운이 다가오는 중";
        }

        if (luckScore <= 80) {
            return "내인생약간상승황동티켓";
        }

        if (luckScore <= 92) {
            return "우주 통신 연결 완료";
        }

        return "외계인도 박수치는 날";
    }
}
