package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.CalculationItem;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.Coefficient;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.CoefficientCondition;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class FireRiskPremiumCalculatorTest {
    private static final RiskPremiumCalculator CALCULATOR = new FireRiskPremiumCalculator();

    private static final BigDecimal TEN = new BigDecimal("10");

    private Coefficient coefficient;

    @BeforeEach
    void setUp() {
        coefficient = new Coefficient(RiskType.FIRE, TEN, TEN, TEN, CoefficientCondition.EQUAL);
    }

    @Test
    void getSupportedRiskType_shouldReturnFire() {
        //when
        RiskType supportedRisk = CALCULATOR.getSupportedRisk();
        //then
        assertThat(supportedRisk).isEqualTo(RiskType.FIRE);
    }

    @Test
    void calculateShouldReturnValidResult() {
        //given
        List<CalculationItem> calculationItems = Stream.of(1, 2, 3, 4)
                                                       .map(BigDecimal::new)
                                                       .map(cost -> new CalculationItem(RiskType.FIRE, cost))
                                                       .toList();
        //when
        BigDecimal calculation = CALCULATOR.calculate(calculationItems, coefficient);

        //then
        assertThat(calculation).isEqualTo(new BigDecimal(100));
    }
}