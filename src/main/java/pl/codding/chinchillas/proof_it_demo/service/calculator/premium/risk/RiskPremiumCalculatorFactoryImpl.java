package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
class RiskPremiumCalculatorFactoryImpl implements RiskPremiumCalculatorFactory {

    private final Map<RiskType, RiskPremiumCalculator> riskCalculators;

    RiskPremiumCalculatorFactoryImpl(List<RiskPremiumCalculator> calculators) {
        this.riskCalculators = calculators.stream()
                                          .collect(toMap(RiskPremiumCalculator::getSupportedRisk, Function.identity()));
    }

    public Optional<RiskPremiumCalculator> getCalculatorForRisk(RiskType riskType) {
        return Optional.ofNullable(riskCalculators.get(riskType));
    }
}
