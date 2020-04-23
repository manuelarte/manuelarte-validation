package io.github.manuelarte.spring.manuelartevalidation.constraints.fromto;

import io.github.manuelarte.spring.manuelartevalidation.constraints.fromto.ToDate.List;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
public @interface ToDate {

  /**
   * To differenciate possible groups when validating From and to dates
   * @return The from and to this annotation belongs to
   */
  String value() default "";

  @Target({ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface List {
    ToDate[] value();
  }

}
