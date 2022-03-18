package pl.codding.chinchillas.proof_it_demo.web.policy;

import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record Policy(
    @NotBlank
    String number,
    @NotBlank
    String status,
    @Valid
    List<PolicyObject> objects
) {

    @Builder
    public Policy(String number, String status, Collection<PolicyObject> objects) {
        this(number, status, new ArrayList<>(objects));
    }
}
