package pl.codding.chinchillas.proof_it_demo.service.calculator;

import pl.codding.chinchillas.proof_it_demo.web.policy.Policy;

import java.math.BigDecimal;

public interface PolicyCalculator {

    BigDecimal calculate(Policy policy);

}
