package pl.codding.chinchillas.proof_it_demo.service.calculator.premium.coefficient;

import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

public class CoefficientTestGenerator {

    public static final String NORMAL = "10.0";

    public static final String MODIFIED = "20.0";

    public static final String THRESHOLD = "100.0";

    public static Coefficient sampleCoefficient(CoefficientCondition condition, RiskType fire) {
        return new Coefficient(fire, NORMAL, MODIFIED, THRESHOLD, condition);
    }

}
