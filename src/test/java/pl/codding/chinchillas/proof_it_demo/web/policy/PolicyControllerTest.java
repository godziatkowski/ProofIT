package pl.codding.chinchillas.proof_it_demo.web.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.codding.chinchillas.proof_it_demo.service.calculator.premium.risk.RiskType;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.codding.chinchillas.proof_it_demo.web.policy.PolicyTestGenerator.*;

@SpringBootTest
@AutoConfigureMockMvc
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @MethodSource("validPolicies")
    void calculatePolicyPremiumCost_shouldReturnOkWithResponse(Policy policy, String expectedValue) throws Exception {
        //given
        String content = objectMapper.writeValueAsString(policy);
        //when
        ResultActions result = mockMvc.perform(post("/api/policies/premium")
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(content));
        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$").isNumber())
              .andExpect(jsonPath("$").value(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("invalidPolicies")
    void calculatePolicyPremiumCost_shouldReturnBadRequest(Policy policy) throws Exception {
        //given
        String content = objectMapper.writeValueAsString(policy);
        //when
        ResultActions result = mockMvc.perform(post("/api/policies/premium")
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(content));
        //then
        result.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidLiabilityCosts")
    void calculatePolicyPremiumCost_shouldReturnBadRequestOnInvalidLiabilityCost(String content) throws Exception {
        //when
        ResultActions result = mockMvc.perform(post("/api/policies/premium")
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(content));
        //then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> validPolicies() {
        PolicySubObject subObjectWithFireCostOf100 = samplePolicySubObject()
            .liabilityCost("100")
            .risks(Set.of(RiskType.FIRE))
            .build();
        PolicySubObject subObjectWithTheftCostOf8 = samplePolicySubObject()
            .liabilityCost("8")
            .risks(Set.of(RiskType.THEFT))
            .build();
        List<PolicySubObject> firstPolicySubObjects = List.of(subObjectWithFireCostOf100, subObjectWithTheftCostOf8);
        PolicyObject firstPolicyObject = samplePolicyObject().subObjects(firstPolicySubObjects).build();
        Policy firstPolicy = samplePolicy().objects(List.of(firstPolicyObject)).build();

        PolicySubObject subObjectWithFireCostOf500 = samplePolicySubObject()
            .liabilityCost("500")
            .risks(Set.of(RiskType.FIRE))
            .build();
        PolicySubObject subObjectWithTheftCostOf102_51 = samplePolicySubObject()
            .liabilityCost("102.51")
            .risks(Set.of(RiskType.THEFT))
            .build();
        List<PolicySubObject> secondPolicySubObjects = List.of(subObjectWithFireCostOf500,
                                                               subObjectWithTheftCostOf102_51);
        PolicyObject secondPolicyObject = samplePolicyObject().subObjects(secondPolicySubObjects).build();
        Policy secondPolicy = samplePolicy().objects(List.of(secondPolicyObject)).build();

        return Stream.of(
            Arguments.of(firstPolicy, "2.28"),
            Arguments.of(secondPolicy, "17.13")
        );
    }

    private static Stream<Policy> invalidPolicies() {
        PolicyObject policyObject = samplePolicyObject().build();
        List<PolicyObject> duplicatedPolicyObjects = List.of(policyObject, policyObject);

        PolicySubObject policySubObject = samplePolicySubObject().build();
        List<PolicySubObject> duplicatedPolicySubObjects = List.of(policySubObject, policySubObject);
        List<PolicySubObject> policySubObjectWithNullName = List.of(samplePolicySubObject().name(null).build());
        List<PolicySubObject> policySubObjectWithEmptyName = List.of(samplePolicySubObject().name("").build());
        List<PolicySubObject> policySubObjectWithNullRisks = List.of(samplePolicySubObject().risks(null).build());
        List<PolicySubObject> policySubObjectWithEmptyRisks = List.of(samplePolicySubObject().risks(Set.of()).build());

        return Stream.of(
            samplePolicy().number(null).build(),
            samplePolicy().number("").build(),
            samplePolicy().status(null).build(),
            samplePolicy().status("").build(),
            samplePolicy().objects(duplicatedPolicyObjects).build(),
            samplePolicy().objects(List.of(samplePolicyObject().name(null).build())).build(),
            samplePolicy().objects(List.of(samplePolicyObject().name("").build())).build(),
            samplePolicy().objects(getObjects(policySubObjectWithNullName)).build(),
            samplePolicy().objects(getObjects(policySubObjectWithEmptyName)).build(),
            samplePolicy().objects(getObjects(policySubObjectWithNullRisks)).build(),
            samplePolicy().objects(getObjects(policySubObjectWithEmptyRisks)).build(),
            samplePolicy().objects(getObjects(duplicatedPolicySubObjects)).build()
        );
    }

    private static List<PolicyObject> getObjects(List<PolicySubObject> policySubObjectWithEmptyName) {
        return List.of(samplePolicyObject().subObjects(policySubObjectWithEmptyName).build());
    }

    private static Stream<String> invalidLiabilityCosts() {
        return Stream.of(
            """
                {
                   "number":"a79afb2c-e1ce-49cb-bde7-620cb482363f",
                   "status":"REGISTERED",
                   "objects":[
                      {
                         "name":"a2b2d21c-ccb1-41b6-ba8e-519d2c425450",
                         "subObjects":[
                            {
                               "name":"name",
                               "liabilityCost":null,
                               "risks":[
                                  "FIRE",
                                  "THEFT"
                               ]
                            }
                         ]
                      }
                   ]
                }""",
            """
                {
                   "number":"a79afb2c-e1ce-49cb-bde7-620cb482363f",
                   "status":"REGISTERED",
                   "objects":[
                      {
                         "name":"a2b2d21c-ccb1-41b6-ba8e-519d2c425450",
                         "subObjects":[
                            {
                               "name":"",
                               "liabilityCost":"",
                               "risks":[
                                  "FIRE",
                                  "THEFT"
                               ]
                            }
                         ]
                      }
                   ]
                }"""
        );
    }
}