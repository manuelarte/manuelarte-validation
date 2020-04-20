package io.github.manuelarte.spring.manuelartevalidation.constraints;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate.List;
import io.github.manuelarte.spring.manuelartevalidation.validators.FromToDateCrossParameterValidator;
import io.github.manuelarte.spring.manuelartevalidation.validators.FromToDateTypeValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintTarget;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = { FromToDateTypeValidator.class, FromToDateCrossParameterValidator.class })
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
public @interface FromToDate {

  enum FromToType {
    FROM_LOWER_THAN_TO, FROM_LOWER_THAN_OR_EQUAL_TO_TO
  }

  FromToType value() default FromToType.FROM_LOWER_THAN_TO;

  String[] identifiers() default {};

  /**
   * The param index from the from and to
   * @return The indexes of the two dates to check
   */
  int[] paramIndex() default {0, 1};

  String message() default "From date and to date not being honored";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  ConstraintTarget validationAppliesTo() default ConstraintTarget.IMPLICIT;

  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface List {
    FromToDate[] value();
  }

}
