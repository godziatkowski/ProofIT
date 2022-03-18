package pl.codding.chinchillas.proof_it_demo.web.policy;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Component
public class PolicyValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Policy.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Policy policy = (Policy) target;
        Set<String> notUniquePolicyObjects = getNotUniquePolicyObjects(policy);
        if (!notUniquePolicyObjects.isEmpty()) {
            errors.rejectValue("objects", "400", "Objects %s are duplicated".formatted(notUniquePolicyObjects));
        }
        Set<String> notUniquePolicySubObjects = getNotUniquePolicySubObjects(policy);
        if (!notUniquePolicySubObjects.isEmpty()) {
            errors.rejectValue("objects", "400",
                               "Items %s in objects are duplicated".formatted(notUniquePolicySubObjects));
        }

    }

    private Set<String> getNotUniquePolicyObjects(Policy policy) {
        Stream<String> stream = policy.objects()
                                      .stream()
                                      .map(PolicyObject::name);
        return getDuplicatedValues(stream);
    }

    private Set<String> getNotUniquePolicySubObjects(Policy policy) {
        Stream<String> stream = policy.objects()
                                      .stream()
                                      .map(PolicyObject::subObjects)
                                      .flatMap(Collection::stream)
                                      .map(PolicySubObject::name);
        return getDuplicatedValues(stream);
    }

    private Set<String> getDuplicatedValues(Stream<String> stringStream) {
        return stringStream.filter(Objects::nonNull)
                           .collect(groupingBy(Function.identity(), counting()))
                           .entrySet()
                           .stream()
                           .filter(entry -> entry.getValue() > 1)
                           .map(Map.Entry::getKey)
                           .collect(Collectors.toSet());
    }

}
