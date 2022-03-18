package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class RiskPremiumCalculatorFactoryImplTest {

    private static final RiskType SUPPORTED_RISK = RiskType.FIRE;

    @Mock
    private RiskPremiumCalculator supportedRiskCalculator;

    @BeforeEach
    void setUp() {
        given(supportedRiskCalculator.getSupportedRisk()).willReturn(SUPPORTED_RISK);
    }

    @Test
    void factoryShouldGetSupportedCalculatorsFromInterfaceBeans() {
        //when
        new RiskPremiumCalculatorFactoryImpl(List.of(supportedRiskCalculator));
        //then
        verify(supportedRiskCalculator).getSupportedRisk();
    }

    @Test
    void factoryShouldReturnSupportedCalculator() {
        //given
        RiskPremiumCalculatorFactoryImpl riskPremiumCalculatorFactory
            = new RiskPremiumCalculatorFactoryImpl(List.of(supportedRiskCalculator));
        //when
        Optional<RiskPremiumCalculator> calculatorForRisk
            = riskPremiumCalculatorFactory.getCalculatorForRisk(supportedRiskCalculator.getSupportedRisk());
        //then
        assertThat(calculatorForRisk).isPresent();
        assertThat(calculatorForRisk.orElseThrow()).isEqualTo(supportedRiskCalculator);
    }

    @ParameterizedTest
    @MethodSource("unsupportedRisks")
    void factoryShouldReturnEmptyOptionalForUnsupportedRiskType(RiskType unsupportedRiskType) {
        //given
        RiskPremiumCalculatorFactoryImpl riskPremiumCalculatorFactory
            = new RiskPremiumCalculatorFactoryImpl(List.of(supportedRiskCalculator));
        //when
        Optional<RiskPremiumCalculator> calculatorForRisk
            = riskPremiumCalculatorFactory.getCalculatorForRisk(unsupportedRiskType);
        //then
        assertThat(calculatorForRisk).isEmpty();
    }

    private static Stream<RiskType> unsupportedRisks() {
        return Stream.of(RiskType.values())
                     .filter(riskType -> riskType != SUPPORTED_RISK);
    }
}