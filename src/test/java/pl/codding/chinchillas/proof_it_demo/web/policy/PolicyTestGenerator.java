package pl.codding.chinchillas.proof_it_demo.web.policy;

import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class PolicyTestGenerator {

    private static final Random RANDOM = new Random();

    private PolicyTestGenerator() {}

    public static PolicySubObject.PolicySubObjectBuilder samplePolicySubObject() {
        return PolicySubObject.builder()
                              .name(UUID.randomUUID().toString())
                              .liabilityCost(String.valueOf(RANDOM.nextDouble()))
                              .risks(Set.of(RiskType.THEFT, RiskType.FIRE));
    }

    public static PolicyObject.PolicyObjectBuilder samplePolicyObject() {
        return PolicyObject.builder()
                           .name(UUID.randomUUID().toString())
                           .subObjects(new ArrayList<>());
    }

    public static Policy.PolicyBuilder samplePolicy() {
        return Policy.builder()
                     .number(UUID.randomUUID().toString())
                     .status("REGISTERED")
                     .objects(new ArrayList<>());
    }

}
