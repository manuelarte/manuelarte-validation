package io.github.manuelarte.spring.manuelartevalidation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.groups.Default;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

public class ExistsValidatorTest {

  @Test
  void testIsValidStringId() {
    final CrudRepository<Object, String> repository = spy(new TestCrudRepository());
    final ExistsValidator existsValidator = new ExistsValidator(Collections.singletonList(repository));

    existsValidator.initialize(createExists(Object.class));
    existsValidator.isValid("id", null);
    verify(repository, times(1)).existsById("id");
  }

  @Test
  void testRepositoryNotFound() {
    final ExistsValidator existsValidator = new ExistsValidator(Collections.emptyList());
    existsValidator.initialize(createExists(Object.class));
    assertFalse(existsValidator.isValid("id", mock(ConstraintValidatorContext.class)));
  }

  @Test
  void testNullId() {
    final CrudRepository<Object, String> repository = spy(new TestCrudRepository());
    final ExistsValidator existsValidator = new ExistsValidator(Collections.singletonList(repository));

    existsValidator.initialize(createExists(Object.class));
    assertTrue(existsValidator.isValid(null, null));
  }

  private Exists createExists(final Class<?> value) {
    return new Exists() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public Class<?> value() {
        return value;
      }

      @Override
      public String message() {
        return null;
      }

      @Override
      public Class<?>[] groups() {
        return new Class[]{Default.class};
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }
    };
  }

  public static class TestCrudRepository implements CrudRepository<Object, String> {

    @Override
    public <S> S save(S entity) {
      return null;
    }

    @Override
    public <S> Iterable<S> saveAll(Iterable<S> entities) {
      return null;
    }

    @Override
    public Optional<Object> findById(String s) {
      return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
      return false;
    }

    @Override
    public Iterable<Object> findAll() {
      return null;
    }

    @Override
    public Iterable<Object> findAllById(Iterable<String> strings) {
      return null;
    }

    @Override
    public long count() {
      return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void deleteAll(Iterable<?> entities) {

    }

    @Override
    public void deleteAll() {

    }
  }

}
