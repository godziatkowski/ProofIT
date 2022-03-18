package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "risk")
class CoefficientProperties {
    private List<Coefficient> coefficients;
}
