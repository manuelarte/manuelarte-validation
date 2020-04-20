package io.github.manuelarte.spring.manuelartevalidation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate.FromToType;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.groups.Default;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FromToDateValidatorTest {

  @Test
  void testFromAndToAreEqualsDateAndValid() {
    final FromToDateValidator fromToDateValidator = new FromToDateValidator();
    fromToDateValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Object[] value = new Object[]{ new Date(), new Date() };
    assertTrue(fromToDateValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testFromAndToAreEqualsDateAndInvalid() {
    final FromToDateValidator fromToDateValidator = new FromToDateValidator();
    fromToDateValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_TO));
    final Object[] value = new Object[]{ new Date(), new Date() };
    assertFalse(fromToDateValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testFromAndToAreDatesAndToIsLower() {
    final FromToDateValidator fromToDateValidator = new FromToDateValidator();
    fromToDateValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Date from = new Date();
    final Date to = new Date(new Date().getTime() - 1000);
    final Object[] value = new Object[]{ from, to };
    assertFalse(fromToDateValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @SuppressWarnings({"UnusedMethod", "JavaTimeDefaultTimeZone"})
  private static Stream<Arguments> isValidTestDataProvider() {
    return Stream.of(
        Arguments.of(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO),
            new Date(), new Date(new Date().getTime() + 1000), true),
        Arguments.of(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO),
            LocalDate.now(), LocalDate.now().plusDays(1), true)
    );
  }

  @ParameterizedTest
  @MethodSource("isValidTestDataProvider")
  void testIsValid(final FromToDate annotation, final Object from, final Object to, boolean expected) {
    final FromToDateValidator fromToDateValidator = new FromToDateValidator();
    fromToDateValidator.initialize(annotation);
    final Object[] value = new Object[]{ from, to };
    assertEquals(expected, fromToDateValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testNotExpectedValues() {
    final FromToDateValidator fromToDateValidator = new FromToDateValidator();
    fromToDateValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Object[] value = new Object[]{ "hola", "adios" };
    assertFalse(fromToDateValidator.isValid(value, mock(ConstraintValidatorContext.class)));
  }

  private static FromToDate createFromToDate(final FromToType value) {
    return new FromToDate() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public FromToType value() {
        return value;
      }

      @Override
      public int[] paramIndex() {
        return new int[] {0, 1};
      }

      @Override
      public String message() {
        return null;
      }

      @Override
      public Class<?>[] groups() {
        return new Class[]{ Default.class };
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }
    };
  }

}
