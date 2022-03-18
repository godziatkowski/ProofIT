package pl.codding.chinchillas.proof_it_demo.web.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public record PolicySubObject(
    @NotBlank
    String name,
    @NotNull
    BigDecimal liabilityCost,
    @NotEmpty
    Set<RiskType> risks
) {

    @Builder
    @JsonCreator
    public PolicySubObject(@JsonProperty("name") @NotBlank String name,
                           @JsonProperty("liabilityCost") @NotBlank String liabilityCost,
                           @JsonProperty("risks") @NotEmpty Set<RiskType> risks) {
        this(name, new BigDecimal(liabilityCost), risks);
    }
}
