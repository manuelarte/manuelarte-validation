package io.github.manuelarte.spring.manuelartevalidation.constraints;

import io.github.manuelarte.spring.manuelartevalidation.validators.ExistsValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ExistsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {

  /**
   * The entity class to check if it exists. It needs to have a Repository interface also.
   * @return the entity that is going to be checked in the repository.
   */
  Class<?> value();

  String message() default "Entity not found";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
