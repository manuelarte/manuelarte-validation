package io.github.manuelarte.spring.manuelartevalidation.validators;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate.FromToType;
import io.github.manuelarte.spring.manuelartevalidation.constraints.fromto.FromDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.fromto.ToDate;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

public class FromToDateTypeValidator implements ConstraintValidator<FromToDate, Object> {

  private FromToType fromToType;
  private Predicate<String> allowedIdentifiers;

  @Override
  public void initialize(final FromToDate constraintAnnotation) {
    this.fromToType = constraintAnnotation.value();
    final Predicate<String> predicate;
    if (constraintAnnotation.identifiers().length == 0) {
      predicate = x -> true;
    } else {
      predicate = x -> Arrays.asList(constraintAnnotation.identifiers()).contains(x);
    }
    this.allowedIdentifiers = predicate;
  }

  @Override
  public boolean isValid(final Object value, final ConstraintValidatorContext context) {
    final MultiValueMap<String, Field> groupedByFromToValue = new LinkedMultiValueMap<>();
    ReflectionUtils.doWithFields(value.getClass(), it -> {
      if (it.getAnnotation(FromDate.List.class) != null) {
        Arrays.stream(it.getAnnotation(FromDate.List.class).value()).forEach(an -> {
          final String group = an.value();
          groupedByFromToValue.add(group, it);
        });
      }
      if (it.getAnnotation(FromDate.class) != null) {
        final FromDate an = it.getAnnotation(FromDate.class);
        final String group = an.value();
        groupedByFromToValue.add(group, it);
      }
      if (it.getAnnotation(ToDate.List.class) != null) {
        Arrays.stream(it.getAnnotation(ToDate.List.class).value()).forEach(an -> {
          final String group = an.value();
          groupedByFromToValue.add(group, it);
        });
      }
      if (it.getAnnotation(ToDate.class) != null) {
        ToDate annotation = it.getAnnotation(ToDate.class);
        final String group = annotation.value();
        groupedByFromToValue.add(group, it);
      }
    });

    return groupedByFromToValue.entrySet().stream().filter(it -> allowedIdentifiers.test(it.getKey()))
        .allMatch(it -> checkFromAndTo(value, it.getKey(), it.getValue(), context));
  }

  @lombok.SneakyThrows
  private boolean checkFromAndTo(final Object entity, final String identifier,
      final List<Field> fields, final ConstraintValidatorContext context) {
    if (fields.size() != 2
        || isFromDate(fields.get(0)) || isFromDate(fields.get(1))
        || isToDate(fields.get(0)) || isToDate(fields.get(1))) {
      context.buildConstraintViolationWithTemplate("Can't find From and To for "
          + "FromAndToDate group " + identifier);
    }
    final Comparable<Object> from;
    final Comparable<Object> to;
    if (isFromDate(fields.get(0))) {
      from = getValue(entity,fields.get(0));
      to = getValue(entity,fields.get(1));
    } else {
      from = getValue(entity,fields.get(1));
      to = getValue(entity,fields.get(0));
    }
    final int expected = this.fromToType == FromToType.FROM_LOWER_THAN_OR_EQUAL_TO_TO ? 0 : 1;
    return to.compareTo(from) >= expected;
  }

  private boolean isFromDate(final Field field) {
    return field.getAnnotation(FromDate.class) != null || field.getAnnotation(FromDate.List.class) != null;
  }
  private boolean isToDate(final Field field) {
    return field.getAnnotation(ToDate.class) != null || field.getAnnotation(ToDate.List.class) != null;
  }

  @lombok.SneakyThrows
  private Comparable<Object> getValue(final Object entity, final Field field) {
    field.setAccessible(true);
    return (Comparable<Object>) field.get(entity);
  }

}
