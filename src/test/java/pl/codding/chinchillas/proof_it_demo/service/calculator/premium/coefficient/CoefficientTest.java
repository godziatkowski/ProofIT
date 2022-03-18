package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.CoefficientTestGenerator.*;

class CoefficientTest {

    @ParameterizedTest
    @MethodSource("getCoefficientLevelForValueParams")
    void getCoefficientLevelForValue_shouldReturnCorrectCoefficient(Coefficient coefficient,
                                                                    BigDecimal valueToCheck,
                                                                    BigDecimal expectedValue) {
        //when
        BigDecimal coefficientLevelForValue = coefficient.getCoefficientLevelForValue(valueToCheck);
        //then
        assertThat(coefficientLevelForValue).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> getCoefficientLevelForValueParams() {
        BigDecimal thresholdValue = new BigDecimal(THRESHOLD);
        return Stream.of(
            Arguments.of(sampleCoefficient(CoefficientCondition.LESS, RiskType.FIRE), thresholdValue.min(ONE), MODIFIED),
            Arguments.of(sampleCoefficient(CoefficientCondition.LESS, RiskType.FIRE), thresholdValue, NORMAL),
            Arguments.of(sampleCoefficient(CoefficientCondition.LESS, RiskType.FIRE), thresholdValue.add(ONE), NORMAL),

            Arguments.of(sampleCoefficient(CoefficientCondition.LESS_OR_EQUAL, RiskType.FIRE), thresholdValue.min(ONE), MODIFIED),
            Arguments.of(sampleCoefficient(CoefficientCondition.LESS_OR_EQUAL, RiskType.FIRE), thresholdValue, MODIFIED),
            Arguments.of(sampleCoefficient(CoefficientCondition.LESS_OR_EQUAL, RiskType.FIRE), thresholdValue.add(ONE), NORMAL),

            Arguments.of(sampleCoefficient(CoefficientCondition.EQUAL, RiskType.FIRE), thresholdValue.min(ONE), NORMAL),
            Arguments.of(sampleCoefficient(CoefficientCondition.EQUAL, RiskType.FIRE), thresholdValue, MODIFIED),
            Arguments.of(sampleCoefficient(CoefficientCondition.EQUAL, RiskType.FIRE), thresholdValue.add(ONE), NORMAL),

            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER_OR_EQUAL, RiskType.FIRE), thresholdValue.min(ONE), NORMAL),
            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER_OR_EQUAL, RiskType.FIRE), thresholdValue, MODIFIED),
            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER_OR_EQUAL, RiskType.FIRE), thresholdValue.add(ONE), MODIFIED),

            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER, RiskType.FIRE), thresholdValue.min(ONE), NORMAL),
            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER, RiskType.FIRE), thresholdValue, NORMAL),
            Arguments.of(sampleCoefficient(CoefficientCondition.GREATER, RiskType.FIRE), thresholdValue.add(ONE), MODIFIED)
        );
    }

}