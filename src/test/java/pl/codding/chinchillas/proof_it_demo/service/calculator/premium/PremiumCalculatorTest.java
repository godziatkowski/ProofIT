package pl.codding.chinchillas.proof_it_demo.service.calculator.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.Coefficient;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.CoefficientCondition;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.RiskCoefficientFactory;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskPremiumCalculator;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskPremiumCalculatorFactory;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;
import pl.codding.chinchillas.proof_it_demo.web.policy.Policy;
import pl.codding.chinchillas.proof_it_demo.web.policy.PolicyObject;
import pl.codding.chinchillas.proof_it_demo.web.policy.PolicySubObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.CoefficientTestGenerator.sampleCoefficient;
import static pl.codding.chinchillas.proof_it_demo.web.policy.PolicyTestGenerator.*;

@ExtendWith(SpringExtension.class)
class PremiumCalculatorTest {

    private static final Coefficient THEFT_COEFFICIENT = sampleCoefficient(CoefficientCondition.EQUAL, RiskType.THEFT);

    private static final Coefficient FIRE_COEFFICIENT = sampleCoefficient(CoefficientCondition.EQUAL, RiskType.FIRE);

    private static final RiskPremiumCalculator FIRE_RISK_CALCULATOR = mock(RiskPremiumCalculator.class);

    private static final RiskPremiumCalculator THEFT_RISK_CALCULATOR = mock(RiskPremiumCalculator.class);

    @Mock
    private RiskPremiumCalculatorFactory calculatorFactory;

    @Mock
    private RiskCoefficientFactory coefficientFactory;

    @Captor
    private ArgumentCaptor<List<CalculationItem>> fireCalculationItemsCaptor;

    @Captor
    private ArgumentCaptor<List<CalculationItem>> theftCalculationItemsCaptor;

    private PremiumCalculator premiumCalculator;

    @BeforeEach
    void setUp() {
        premiumCalculator = new PremiumCalculator(coefficientFactory, calculatorFactory);
    }

    @Test
    void calculate_shouldProperlyGroupCalculationItems() {
        //given
        PolicySubObject theft = samplePolicySubObject().liabilityCost("1").risks(Set.of(RiskType.THEFT)).build();
        PolicySubObject fire = samplePolicySubObject().liabilityCost("2").risks(Set.of(RiskType.FIRE)).build();
        PolicySubObject bothRisks = samplePolicySubObject().liabilityCost("3")
                                                           .risks(Set.of(RiskType.THEFT, RiskType.FIRE))
                                                           .build();

        PolicyObject firstPolicyObject = samplePolicyObject().subObjects(List.of(theft, fire)).build();
        PolicyObject secondPolicyObject = samplePolicyObject().subObjects(List.of(bothRisks)).build();

        Policy policy = samplePolicy().objects(List.of(firstPolicyObject, secondPolicyObject)).build();

        given(coefficientFactory.getCoefficientForRisk(RiskType.FIRE)).willReturn(Optional.of(FIRE_COEFFICIENT));
        given(coefficientFactory.getCoefficientForRisk(RiskType.THEFT)).willReturn(Optional.of(THEFT_COEFFICIENT));
        given(calculatorFactory.getCalculatorForRisk(RiskType.FIRE)).willReturn(Optional.of(FIRE_RISK_CALCULATOR));
        given(calculatorFactory.getCalculatorForRisk(RiskType.THEFT)).willReturn(Optional.of(THEFT_RISK_CALCULATOR));

        given(FIRE_RISK_CALCULATOR.calculate(anyList(), eq(FIRE_COEFFICIENT))).willReturn(new BigDecimal("25.5"));
        given(THEFT_RISK_CALCULATOR.calculate(anyList(), eq(THEFT_COEFFICIENT))).willReturn(new BigDecimal("24.5"));
        BigDecimal expectedResult = new BigDecimal("50.0");

        //when
        BigDecimal result = premiumCalculator.calculate(policy);

        //then
        assertThat(result).isEqualTo(expectedResult);

        verify(FIRE_RISK_CALCULATOR).calculate(fireCalculationItemsCaptor.capture(), eq(FIRE_COEFFICIENT));
        verify(THEFT_RISK_CALCULATOR).calculate(theftCalculationItemsCaptor.capture(), eq(THEFT_COEFFICIENT));

        List<CalculationItem> fireCalculationItems = fireCalculationItemsCaptor.getValue();
        assertThat(fireCalculationItems).extracting("riskType").containsOnly(RiskType.FIRE, RiskType.FIRE);
        assertThat(fireCalculationItems).extracting("value").contains(fire.liabilityCost(), bothRisks.liabilityCost());

        List<CalculationItem> theftCalculationItems = theftCalculationItemsCaptor.getValue();
        assertThat(theftCalculationItems).extracting("riskType").containsOnly(RiskType.THEFT, RiskType.THEFT);
        assertThat(theftCalculationItems).extracting("value")
                                         .contains(theft.liabilityCost(), bothRisks.liabilityCost());
    }

    @ParameterizedTest
    @MethodSource("optionalValues")
    void calculate_shouldReturnZero_whenNeitherOfRisksIsFullySupported(Optional<Coefficient> fireCoefficient,
                                                                       Optional<Coefficient> theftCoefficient,
                                                                       Optional<RiskPremiumCalculator> fireRiskCalculator,
                                                                       Optional<RiskPremiumCalculator> theftRiskCalculator) {
        //given
        List<PolicySubObject> policySubObjects = List.of(
            samplePolicySubObject().risks(Set.of(RiskType.THEFT, RiskType.FIRE)).build());
        List<PolicyObject> policyObjects = List.of(samplePolicyObject().subObjects(policySubObjects).build());
        Policy policy = samplePolicy().objects(policyObjects).build();

        given(coefficientFactory.getCoefficientForRisk(RiskType.FIRE)).willReturn(fireCoefficient);
        given(coefficientFactory.getCoefficientForRisk(RiskType.THEFT)).willReturn(theftCoefficient);
        given(calculatorFactory.getCalculatorForRisk(RiskType.FIRE)).willReturn(fireRiskCalculator);
        given(calculatorFactory.getCalculatorForRisk(RiskType.THEFT)).willReturn(theftRiskCalculator);
        //when
        BigDecimal calculation = premiumCalculator.calculate(policy);
        //then
        assertThat(calculation).isEqualTo(new BigDecimal("0.0"));
    }

    private static Stream<Arguments> optionalValues() {
        return Stream.of(
            Arguments.of(Optional.empty(), Optional.of(THEFT_COEFFICIENT),
                         Optional.of(FIRE_RISK_CALCULATOR), Optional.empty()),
            Arguments.of(Optional.of(FIRE_COEFFICIENT), Optional.empty(),
                         Optional.empty(), Optional.of(THEFT_RISK_CALCULATOR)),
            Arguments.of(Optional.empty(), Optional.empty(),
                         Optional.empty(), Optional.empty())
        );
    }

}