package pl.codding.chinchillas.proof_it_demo.service.calculator.premium;

import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.math.BigDecimal;

public record CalculationItem(RiskType riskType, BigDecimal value) {
}
