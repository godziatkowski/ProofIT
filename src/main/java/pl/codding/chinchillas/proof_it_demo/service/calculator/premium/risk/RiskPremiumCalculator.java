package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient.Coefficient;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.CalculationItem;

import java.math.BigDecimal;
import java.util.List;

public interface RiskPremiumCalculator {

    RiskType getSupportedRisk();

    BigDecimal calculate(List<CalculationItem> calculationItems, Coefficient coefficient);
}
