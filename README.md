## ProofIT demo application 

Starting application can be done using `mvn spring-boot:run` command

Using exposed API can be done by importing and using requests from Postman collection with samples


### Solution description
1. Data structure

- [Policy](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/web/policy/Policy.java) 
  - number - string value representing policy
  - status - string value representing policy status
  - objects - collection of PolicyObject's that can represent group of items that should be included in policy
- [PolicyObject](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/web/policy/PolicyObject.java)
  - name - name of group, for example `House`. Only one PolicyObject with given name is allowed in Policy
  - subObjects - collection of PolicySubObject's, items that should be included in policy
- [PolicySubObject](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/web/policy/PolicySubObject.java)
  - name - value that represent an item. Each item can be added only once in whole Policy
  - liabilityCost - item value in Euro currency
  - risks - collection of RiskType's that given item can be exposed to
- [RiskType](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/risk/RiskType.java)
  - currently possible values are THEFT and FIRE

3. [PolicyCalculator](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/PolicyCalculator.java)

In current state core of functionality, allows for implementing different types of calculators for Policy.
Have single method that expects a `Policy` objects and returns a `BigDecimal` value.
In future if more complex result types would be required, could be modified to return some more sophisticated object
that by default contains `BigDecimal` as some result but may contain additional fields.

2. 1. [PremiumCalculator](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/PremiumCalculator.java)

Implementation of `PolicyCalculator`, splits PolicySubObjects based on their possible risks, 
calculates premium cost of policy per risk type and returns cost after summing value for each risk type     
 
3. [Coefficient](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/coefficient/Coefficient.java) 

Configuration class that defines coefficient values for each risk type. Configurable on runtime using properties.
Each coefficient for risk type contains default value (`normal`) and value which should be used if condition is met (`modified`). 
Condition is based on threshold value and condition type defined as one of following values: 
- LESS
- LESS_OR_EQUAL
- EQUAL
- GREATER_OR_EQUAL
- GREATER

3. 1. [RiskCoefficientFactory](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/coefficient/RiskCoefficientFactory.java) and 
[RiskCoefficientFactoryImpl](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/coefficient/RiskCoefficientFactoryImpl.java)

Supplier of Coefficient value using Strategy pattern. Returns optional Coefficient if it is known.

4. [RiskPremiumCalculator](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/risk/RiskPremiumCalculator.java)

Interface representing premium calculator for separate risk types. Expects list of values that should be 
used for calculations and a Coefficient that should be applied.  

4. 1. [RiskPremiumCalculatorFactory](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/risk/RiskPremiumCalculatorFactory.java) and 
[RiskPremiumCalculatorFactoryImpl](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/service/calculator/premium/risk/RiskPremiumCalculatorFactoryImpl.java)

Factory of RiskPremiumCalculators, returns optional calculator for given risk type if it's supported

5. [PolicyValidator](https://github.com/godziatkowski/ProofIT/blob/master/src/main/java/pl/codding/chinchillas/proof_it_demo/web/policy/PolicyValidator.java)

Validator that ensures that Policy has unique objects

### Extending logic

Adding new risk type requires 

- extending RiskType enum 
- adding new properties to provide values required for risk type coefficients
- implementing new RiskPremiumCalculator for given RiskType

Adding different type of PolicyCalculator - for example one that ignores risks, and simply sums up PolicySubObjects costs

- create new package for new type of calculator 
- implement new calculator logic
- inject required implementation using Qualifier annotation

### Potential modifications and improvements

- PolicyValidator => could be replaced with some annotation based validation though it would still need to work on 
collection of PolicyObjects in Policy and not directly on PolicySubObjects
- RiskPremiumCalculator implementations and factory => both types of risk premium calculators use same logic. 
  - both Fire and Theft calculators use same logic - it could be extracted to some shared calculator. I ignored this as 
I wanted to show implementation per risk type
  - if fire and theft calculators would be replaced with some default one factory could always return an instance of 
RiskPremiumCalculator instead of optional. It could work like 
    - if risk type has its own calculator => return specific calculator 
    - if risk type doesn't have unique calculator => return default calculator
- Policy, PolicyObject, PolicySubObjects and their fields naming convention. I decided to mostly follow the
task description, but maybe there are better names that would properly show what kind of element is stored 
in given object or field
- documentation done using SpringDocs or Swagger instead of README.md file