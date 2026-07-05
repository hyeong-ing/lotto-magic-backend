package com.lottomagic.lotto.service;

import com.lottomagic.lotto.dto.LottoOptionsResponse;
import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.dto.LottoResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
public class LottoService {

    private static final int LOTTO_MIN_NUMBER = 1;
    private static final int LOTTO_MAX_NUMBER = 45;
    private static final int LOTTO_NUMBER_COUNT = 6;
    private static final int SPELL_COUNT = 9;

    private static final List<String> OPTION_ITEMS = List.of(
            "행운",
            "조상님의도움",
            "제왕의자리",
            "개쩌는꿈",
            "나의직감",
            "엘프의선견지명",
            "한치앞이보이는내인생",
            "내돈",
            "다이아몬드광산주인",
            "개꿈",
            "내인생수직상승황금티켓",
            "요정님도와죠",
            "내집마련",
            "외계인의텔레파시",
            "퇴사각",
            "1등이필요해"
    );

    private static final Map<String, Integer> OPTION_SCORES = Map.ofEntries(
            Map.entry("개꿈", 2),
            Map.entry("나의직감", 2),
            Map.entry("내돈", 4),
            Map.entry("행운", 4),
            Map.entry("개쩌는꿈", 5),
            Map.entry("퇴사각", 5),
            Map.entry("내집마련", 5),
            Map.entry("조상님의도움", 6),
            Map.entry("요정님도와죠", 6),
            Map.entry("엘프의선견지명", 7),
            Map.entry("한치앞이보이는내인생", 7),
            Map.entry("외계인의텔레파시", 7),
            Map.entry("내인생수직상승황금티켓", 8),
            Map.entry("다이아몬드광산주인", 8),
            Map.entry("1등이필요해", 9),
            Map.entry("제왕의자리", 9)
    );

    private final Random random;
    private final Clock clock;

    public LottoService() {
        this(new Random(), Clock.systemDefaultZone());
    }

    LottoService(Random random, Clock clock) {
        this.random = random;
        this.clock = clock;
    }

    public LottoOptionsResponse getOptions() {
        return new LottoOptionsResponse(OPTION_ITEMS);
    }

    public LottoResponse draw(LottoRequest request) {
        List<String> selectedOptions = request.selectedOptions();

        validateSelectedOptions(selectedOptions);

        int selectedOptionScore = calculateSelectedOptionScore(selectedOptions);

        List<Integer> numbers = generateLottoNumbers(selectedOptionScore);
        int luckScore = generateLuckScore(selectedOptionScore);
        String luckMessage = pickLuckMessage(luckScore);

        int spellNumber = generateSpellNumber();
        String spellImageUrl = generateSpellImageUrl(spellNumber);

        return new LottoResponse(
                numbers,
                luckScore,
                luckMessage,
                selectedOptions,
                spellNumber,
                spellImageUrl
        );
    }

    private void validateSelectedOptions(List<String> selectedOptions) {
        if (selectedOptions == null || selectedOptions.size() != 3) {
            throw new IllegalArgumentException("요소는 정확히 3개 선택해야 합니다.");
        }

        boolean hasBlankOption = selectedOptions.stream()
                .anyMatch(option -> option == null || option.isBlank());

        if (hasBlankOption) {
            throw new IllegalArgumentException("선택 요소 이름은 비어 있을 수 없습니다.");
        }

        boolean hasInvalidOption = selectedOptions.stream()
                .anyMatch(option -> !OPTION_ITEMS.contains(option));

        if (hasInvalidOption) {
            throw new IllegalArgumentException("존재하지 않는 선택 요소가 포함되어 있습니다.");
        }

        long distinctCount = selectedOptions.stream()
                .distinct()
                .count();

        if (distinctCount != selectedOptions.size()) {
            throw new IllegalArgumentException("선택 요소는 중복될 수 없습니다.");
        }
    }

    private int calculateSelectedOptionScore(List<String> selectedOptions) {
        return selectedOptions.stream()
                .mapToInt(OPTION_SCORES::get)
                .sum();
    }

    private List<Integer> generateLottoNumbers(int selectedOptionScore) {
        Set<Integer> randomIndexes = new LinkedHashSet<>();

        while (randomIndexes.size() < LOTTO_NUMBER_COUNT) {
            int randomIndex = random.nextInt(LOTTO_MAX_NUMBER);
            randomIndexes.add(randomIndex);
        }

        List<Integer> numbers = new ArrayList<>();

        for (int randomIndex : randomIndexes) {
            int movedIndex = moveIndexByOptionScore(randomIndex, selectedOptionScore);
            int lottoNumber = movedIndex + LOTTO_MIN_NUMBER;

            numbers.add(lottoNumber);
        }

        Collections.sort(numbers);

        return numbers;
    }

    private int moveIndexByOptionScore(int randomIndex, int selectedOptionScore) {
        return (randomIndex + selectedOptionScore) % LOTTO_MAX_NUMBER;
    }

    private int generateLuckScore(int selectedOptionScore) {
        int baseScore = random.nextInt(51) + 10;
        int todayLastDigit = LocalDate.now(clock).getDayOfMonth() % 10;
        int randomAdjustment = random.nextInt(11) - 5;

        int luckScore = baseScore
                + selectedOptionScore
                + todayLastDigit
                + randomAdjustment;

        return clamp(luckScore, 0, 100);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private String pickLuckMessage(int luckScore) {
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

    private int generateSpellNumber() {
        return random.nextInt(SPELL_COUNT) + 1;
    }

    private String generateSpellImageUrl(int spellNumber) {
        return "/" + spellNumber + ".png";
    }
}
