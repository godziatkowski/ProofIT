package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import java.util.Optional;

public interface RiskPremiumCalculatorFactory{
    Optional<RiskPremiumCalculator> getCalculatorForRisk(RiskType riskType);
}
