package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.CoefficientTestGenerator.sampleCoefficient;

@ExtendWith(SpringExtension.class)
class RiskCoefficientFactoryImplTest {

    private static final Coefficient SUPPORTED_COEFFICIENT = sampleCoefficient(CoefficientCondition.EQUAL,
                                                                               RiskType.FIRE);

    @Mock
    private CoefficientProperties coefficientProperties;

    @BeforeEach
    void setUp() {
        given(coefficientProperties.getCoefficients()).willReturn(List.of(SUPPORTED_COEFFICIENT));
    }

    @Test
    void factoryShouldGetSupportedCoefficientsFromProperties() {
        //when
        new RiskCoefficientFactoryImpl(coefficientProperties);
        //then
        verify(coefficientProperties).getCoefficients();
    }

    @Test
    void factoryShouldReturnCoefficientWhenItIsSupported() {
        //given
        RiskCoefficientFactoryImpl riskCoefficientFactory = new RiskCoefficientFactoryImpl(coefficientProperties);
        //when
        Optional<Coefficient> optionalValue = riskCoefficientFactory.getCoefficientForRisk(
            SUPPORTED_COEFFICIENT.riskType());
        //then
        assertThat(optionalValue).isPresent();
        assertThat(optionalValue.orElseThrow()).isEqualTo(SUPPORTED_COEFFICIENT);
    }

    @ParameterizedTest
    @MethodSource("unsupportedCoefficients")
    void factoryShouldReturnEmptyOptionalForUnsupportedCoefficient(RiskType riskType) {
        //given
        RiskCoefficientFactoryImpl riskCoefficientFactory = new RiskCoefficientFactoryImpl(coefficientProperties);
        //when
        Optional<Coefficient> optionalValue = riskCoefficientFactory.getCoefficientForRisk(
            riskType);
        //then
        assertThat(optionalValue).isEmpty();
    }

    private static Stream<RiskType> unsupportedCoefficients() {
        return Stream.of(RiskType.values())
                     .filter(risk -> risk != SUPPORTED_COEFFICIENT.riskType());
    }
}