package pl.codding.chinchillas.proof_it_demo.web.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.codding.chinchillas.proof_it_demo.service.calculator.PolicyCalculator;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Validated
@RestController
class PolicyController {

    private final PolicyCalculator premiumCalculator;

    private final PolicyValidator policyValidator;

    public PolicyController(@Qualifier("premiumCalculator") PolicyCalculator premiumCalculator,
                            PolicyValidator policyValidator) {
        this.premiumCalculator = premiumCalculator;
        this.policyValidator = policyValidator;
    }

    @InitBinder("policy")
    protected void initPolicyBinder(WebDataBinder binder) {
        binder.setValidator(policyValidator);
    }

    @PostMapping("/api/policies/premium")
    BigDecimal calculatePolicyPremiumCost(@Valid @RequestBody Policy policy) {
        log.info("Received request to calculate premium cost for policy <{}>", policy.number());
        BigDecimal premiumCost = premiumCalculator.calculate(policy);
        log.info("Premium cost of policy <{}> calculated as <{}>", policy.number(), premiumCost);
        return premiumCost.setScale(2, RoundingMode.HALF_EVEN);
    }
}
