package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.util.Optional;

public interface RiskCoefficientFactory {

    Optional<Coefficient> getCoefficientForRisk(RiskType riskType);
}
