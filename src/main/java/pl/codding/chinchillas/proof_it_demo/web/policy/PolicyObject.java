package pl.codding.chinchillas.proof_it_demo.web.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record PolicyObject(
    @NotBlank
    String name,
    @Valid
    List<PolicySubObject> subObjects
) {

    @Builder
    @JsonCreator
    public PolicyObject(String name, Collection<PolicySubObject> subObjects) {
        this(name, new ArrayList<>(subObjects));
    }
}
