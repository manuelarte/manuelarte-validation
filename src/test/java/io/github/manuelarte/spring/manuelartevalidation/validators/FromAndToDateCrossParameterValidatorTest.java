package io.github.manuelarte.spring.manuelartevalidation.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromAndToDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.FromAndToDate.FromToType;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.groups.Default;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FromAndToDateCrossParameterValidatorTest {

  @Test
  void testFromAndToAreEqualsDateAndValid() {
    final FromToDateCrossParameterValidator fromToDateCrossParameterValidator = new FromToDateCrossParameterValidator();
    fromToDateCrossParameterValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Object[] value = new Object[]{ new Date(), new Date() };
    assertTrue(fromToDateCrossParameterValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testFromAndToAreEqualsDateAndInvalid() {
    final FromToDateCrossParameterValidator fromToDateCrossParameterValidator = new FromToDateCrossParameterValidator();
    fromToDateCrossParameterValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_TO));
    final Object[] value = new Object[]{ new Date(), new Date() };
    assertFalse(fromToDateCrossParameterValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testFromAndToAreDatesAndToIsLower() {
    final FromToDateCrossParameterValidator fromToDateCrossParameterValidator = new FromToDateCrossParameterValidator();
    fromToDateCrossParameterValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Date from = new Date();
    final Date to = new Date(new Date().getTime() - 1000);
    final Object[] value = new Object[]{ from, to };
    assertFalse(fromToDateCrossParameterValidator.isValid(value, mock(
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
  void testIsValid(final FromAndToDate annotation, final Object from, final Object to, boolean expected) {
    final FromToDateCrossParameterValidator fromToDateCrossParameterValidator = new FromToDateCrossParameterValidator();
    fromToDateCrossParameterValidator.initialize(annotation);
    final Object[] value = new Object[]{ from, to };
    assertEquals(expected, fromToDateCrossParameterValidator.isValid(value, mock(
        ConstraintValidatorContext.class)));
  }

  @Test
  void testNotExpectedValues() {
    final FromToDateCrossParameterValidator fromToDateCrossParameterValidator = new FromToDateCrossParameterValidator();
    fromToDateCrossParameterValidator.initialize(createFromToDate(FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO));
    final Object[] value = new Object[]{ "hola", "adios" };
    assertFalse(fromToDateCrossParameterValidator.isValid(value, mock(ConstraintValidatorContext.class)));
  }

  private static FromAndToDate createFromToDate(final FromToType value) {
    return new FromAndToDate() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public FromToType value() {
        return value;
      }

      @Override
      public String[] identifiers() {
        return new String[0];
      }

      @Override
      public int[] paramIndexes() {
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

      @Override
      public ConstraintTarget validationAppliesTo() {
        return ConstraintTarget.IMPLICIT;
      }
    };
  }

}
