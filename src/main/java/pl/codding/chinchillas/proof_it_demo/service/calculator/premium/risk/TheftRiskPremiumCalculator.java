package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.CalculationItem;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.Coefficient;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
class TheftRiskPremiumCalculator implements RiskPremiumCalculator {
    @Override
    public RiskType getSupportedRisk() {
        return RiskType.THEFT;
    }

    @Override
    public BigDecimal calculate(List<CalculationItem> calculationItems, Coefficient coefficient) {
        BigDecimal result = calculationItems.stream()
                                            .map(CalculationItem::value)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal coefficientValue = coefficient.getCoefficientLevelForValue(result);
        BigDecimal cost = result.multiply(coefficientValue);
        log.info("Sum insured for <{}> risk is equal to <{}>. Using coefficient <{}>. Premium cost is <{}>",
                 RiskType.THEFT, result, coefficientValue, cost);
        return cost;
    }
}
