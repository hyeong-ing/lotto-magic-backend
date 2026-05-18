package com.lottomagic.lotto.service;

import com.lottomagic.lotto.dto.LottoRequest;
import com.lottomagic.lotto.dto.LottoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LottoDistributionTest {

    /*
        여기만 바꾸면 원하는 선택 요소 조합으로 분포를 확인할 수 있다.

        예:
        List.of("개꿈", "나의직감", "내돈")
        List.of("행운", "조상님의도움", "외계인의텔레파시")
        List.of("제왕의자리", "1등이필요해", "다이아몬드광산주인")
    */
    private static final List<String> SELECTED_OPTIONS = List.of(
            "외계인의텔레파시",
            "다이아몬드광산주인",
            "1등이필요해"
    );

    /*
        분포 확인을 몇 번 돌릴지 정한다.
        1,000번은 가볍게 확인용.
        10,000번은 분포를 꽤 안정적으로 보기 좋다.
    */
    private static final int SIMULATION_COUNT = 10_000;

    /*
        처음 몇 개의 결과를 샘플로 출력할지 정한다.
        번호, 점수, 메시지, 주문진 이미지를 직접 눈으로 확인하기 위한 용도다.
    */
    private static final int SAMPLE_PRINT_COUNT = 10;

    /*
        테스트 날짜를 고정한다.

        행운 점수 계산식에 "오늘 날짜 마지막 자리"가 들어가기 때문에,
        날짜를 고정하지 않으면 매일 결과 분포가 조금씩 달라진다.
    */
    private static final ZoneId TEST_ZONE = ZoneId.of("Asia/Seoul");
    private static final Clock FIXED_CLOCK = Clock.fixed(
            LocalDate.of(2026, 5, 16)
                    .atStartOfDay(TEST_ZONE)
                    .toInstant(),
            TEST_ZONE
    );

    @Test
    @DisplayName("선택 요소 조합에 따른 행운 점수 분포를 확인한다")
    void checkLuckScoreDistributionBySelectedOptions() {
        // given
        Random fixedRandom = new Random(20260516L);
        LottoService lottoService = new LottoService(fixedRandom, FIXED_CLOCK);
        LottoRequest request = new LottoRequest(SELECTED_OPTIONS);

        int[] scoreBuckets = new int[10];

        Map<String, Integer> messageCounts = new LinkedHashMap<>();
        messageCounts.put("우주 와이파이 끊김", 0);
        messageCounts.put("조상님이 애쓰는 중", 0);
        messageCounts.put("요정의 행운이 다가오는 중", 0);
        messageCounts.put("내인생약간상승황동티켓", 0);
        messageCounts.put("우주 통신 연결 완료", 0);
        messageCounts.put("외계인도 박수치는 날", 0);

        List<Integer> scores = new ArrayList<>();
        List<String> samples = new ArrayList<>();

        // when
        for (int count = 1; count <= SIMULATION_COUNT; count++) {
            LottoResponse response = lottoService.draw(request);

            int luckScore = response.luckScore();
            String luckMessage = response.luckMessage();

            scores.add(luckScore);

            int bucketIndex = Math.min(luckScore / 10, 9);
            scoreBuckets[bucketIndex]++;

            messageCounts.put(
                    luckMessage,
                    messageCounts.getOrDefault(luckMessage, 0) + 1
            );

            if (count <= SAMPLE_PRINT_COUNT) {
                samples.add(
                        "%02d회 | 번호=%s | 행운점수=%d | 메시지=%s | 주문진=%d | 이미지=%s"
                                .formatted(
                                        count,
                                        response.numbers(),
                                        response.luckScore(),
                                        response.luckMessage(),
                                        response.spellNumber(),
                                        response.spellImageUrl()
                                )
                );
            }
        }

        // then
        int minScore = scores.stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElseThrow();

        int maxScore = scores.stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElseThrow();

        double averageScore = scores.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElseThrow();

        long lowScoreCount = scores.stream()
                .filter(score -> score <= 20)
                .count();

        long highScoreCount = scores.stream()
                .filter(score -> score >= 81)
                .count();

        double lowScoreRate = toPercent(lowScoreCount, SIMULATION_COUNT);
        double highScoreRate = toPercent(highScoreCount, SIMULATION_COUNT);

        assertEquals(SIMULATION_COUNT, scores.size());
        assertTrue(minScore >= 0);
        assertTrue(maxScore <= 100);

        printSimulationResult(
                samples,
                scoreBuckets,
                messageCounts,
                minScore,
                maxScore,
                averageScore,
                lowScoreCount,
                lowScoreRate,
                highScoreCount,
                highScoreRate
        );
    }

    private double toPercent(long count, int totalCount) {
        return (count * 100.0) / totalCount;
    }

    private void printSimulationResult(
            List<String> samples,
            int[] scoreBuckets,
            Map<String, Integer> messageCounts,
            int minScore,
            int maxScore,
            double averageScore,
            long lowScoreCount,
            double lowScoreRate,
            long highScoreCount,
            double highScoreRate
    ) {
        System.out.println();
        System.out.println("======================================");
        System.out.println("행운 점수 분포 확인");
        System.out.println("======================================");
        System.out.println("선택 요소: " + SELECTED_OPTIONS);
        System.out.println("반복 횟수: " + SIMULATION_COUNT);
        System.out.println();

        System.out.println("[샘플 결과]");
        samples.forEach(System.out::println);
        System.out.println();

        System.out.println("[점수 요약]");
        System.out.println("최저 점수: " + minScore);
        System.out.println("최고 점수: " + maxScore);
        System.out.println("평균 점수: %.2f".formatted(averageScore));
        System.out.println("저점 비율(0~20): %d회 / %.2f%%".formatted(lowScoreCount, lowScoreRate));
        System.out.println("고점 비율(81~100): %d회 / %.2f%%".formatted(highScoreCount, highScoreRate));
        System.out.println();

        System.out.println("[점수 구간별 분포]");
        for (int index = 0; index < scoreBuckets.length; index++) {
            String label = getBucketLabel(index);
            int count = scoreBuckets[index];
            double percent = toPercent(count, SIMULATION_COUNT);
            String bar = "█".repeat((int) Math.round(percent / 2));

            System.out.println(
                    "%s | %5d회 | %6.2f%% | %s"
                            .formatted(label, count, percent, bar)
            );
        }

        System.out.println();

        System.out.println("[행운 메시지별 분포]");
        for (Map.Entry<String, Integer> entry : messageCounts.entrySet()) {
            String message = entry.getKey();
            int count = entry.getValue();
            double percent = toPercent(count, SIMULATION_COUNT);
            String bar = "█".repeat((int) Math.round(percent / 2));

            System.out.println(
                    "%s | %5d회 | %6.2f%% | %s"
                            .formatted(message, count, percent, bar)
            );
        }

        System.out.println("======================================");
        System.out.println();
    }

    private String getBucketLabel(int index) {
        if (index == 9) {
            return "90~100";
        }

        int start = index * 10;
        int end = start + 9;

        return "%02d~%02d".formatted(start, end);
    }
}