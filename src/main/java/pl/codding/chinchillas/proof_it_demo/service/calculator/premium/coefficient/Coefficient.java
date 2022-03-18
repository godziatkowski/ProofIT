package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import org.springframework.boot.context.properties.ConstructorBinding;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.math.BigDecimal;

public record Coefficient(RiskType riskType,
                          BigDecimal normal,
                          BigDecimal modified,
                          BigDecimal threshold,
                          CoefficientCondition condition) {

    @ConstructorBinding
    public Coefficient(RiskType riskType,
                       String normal,
                       String modified,
                       String threshold,
                       CoefficientCondition condition) {
        this(riskType, new BigDecimal(normal), new BigDecimal(modified), new BigDecimal(threshold), condition);
    }

    public BigDecimal getCoefficientLevelForValue(BigDecimal value) {
        boolean thresholdExceeded = switch (condition) {
            case LESS -> value.compareTo(threshold) < 0;
            case LESS_OR_EQUAL -> value.compareTo(threshold) <= 0;
            case EQUAL -> value.compareTo(threshold) == 0;
            case GREATER_OR_EQUAL -> value.compareTo(threshold) >= 0;
            case GREATER -> value.compareTo(threshold) > 0;
        };
        return thresholdExceeded ? modified : normal;
    }
}
