package io.github.manuelarte.spring.manuelartevalidation.constraints;

import io.github.manuelarte.spring.manuelartevalidation.validators.FromToDateValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = FromToDateValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface FromToDate {

  enum FromToType {
    FROM_LOWER_THAN_TO, FROM_LOWER_THAN_OR_EQUAL_TO_TO
  }

  FromToType value() default FromToType.FROM_LOWER_THAN_TO;

  /**
   * The param index from the from and to
   * @return The indexes of the two dates to check
   */
  int[] paramIndex() default {0, 1};

  String message() default "From date and to date don't match";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
