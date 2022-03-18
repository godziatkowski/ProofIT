package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import org.springframework.stereotype.Service;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
class RiskCoefficientFactoryImpl implements RiskCoefficientFactory {

    private final Map<RiskType, Coefficient> riskTypeCoefficientMap;

    RiskCoefficientFactoryImpl(CoefficientProperties coefficientProperties) {
        riskTypeCoefficientMap = coefficientProperties.getCoefficients()
                                                      .stream()
                                                      .collect(toMap(Coefficient::riskType, Function.identity()));
    }

    public Optional<Coefficient> getCoefficientForRisk(RiskType riskType) {
        return Optional.ofNullable(riskTypeCoefficientMap.get(riskType));
    }
}
