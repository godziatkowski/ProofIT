package pl.codding.chinchillas.proof_it_demo.service.calculator.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.codding.chinchillas.proof_it_demo.service.calculator.PolicyCalculator;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.Coefficient;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.RiskCoefficientFactory;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskPremiumCalculator;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskPremiumCalculatorFactory;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;
import pl.codding.chinchillas.proof_it_demo.web.policy.Policy;
import pl.codding.chinchillas.proof_it_demo.web.policy.PolicyObject;
import pl.codding.chinchillas.proof_it_demo.web.policy.PolicySubObject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumCalculator implements PolicyCalculator {

    private final RiskCoefficientFactory riskCoefficientFactory;

    private final RiskPremiumCalculatorFactory riskPremiumCalculatorFactory;

    public BigDecimal calculate(Policy policy) {
        Map<RiskType, List<CalculationItem>> riskCalculationItems = getRiskCalculationItems(policy);
        return riskCalculationItems.entrySet()
                                   .stream()
                                   .map(entrySet -> calculateRiskCost(entrySet.getKey(), entrySet.getValue()))
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<RiskType, List<CalculationItem>> getRiskCalculationItems(Policy policy) {
        return policy.objects()
                     .stream()
                     .map(PolicyObject::subObjects)
                     .flatMap(Collection::stream)
                     .flatMap(this::mapSubItemToCalculationItems)
                     .collect(groupingBy(CalculationItem::riskType, toList()));
    }

    private Stream<CalculationItem> mapSubItemToCalculationItems(PolicySubObject subObject) {
        return subObject.risks()
                        .stream()
                        .map(riskType -> new CalculationItem(riskType, subObject.liabilityCost()));
    }

    private BigDecimal calculateRiskCost(RiskType riskType, List<CalculationItem> calculationItems) {
        BigDecimal cost = new BigDecimal("0.0");
        Optional<RiskPremiumCalculator> calculator = riskPremiumCalculatorFactory.getCalculatorForRisk(riskType);
        Optional<Coefficient> coefficient = riskCoefficientFactory.getCoefficientForRisk(riskType);
        if (calculator.isPresent() && coefficient.isPresent()) {
            cost = calculateCostForSupportedRisk(calculator.get(), coefficient.get(), calculationItems);
        } else {
            log.warn("Premium risk <{}> calculation not supported. Calculator found: <{}>. Coefficient found: <{}>",
                     riskType, calculator.isPresent(), coefficient.isPresent());
        }
        return cost;
    }

    private BigDecimal calculateCostForSupportedRisk(RiskPremiumCalculator calculator,
                                                     Coefficient coefficient,
                                                     List<CalculationItem> calculationItems) {
        return calculator.calculate(calculationItems, coefficient);
    }

}
