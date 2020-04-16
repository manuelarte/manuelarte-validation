package io.github.manuelarte.spring.manuelartevalidation.validators;

import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExistsValidator implements ConstraintValidator<Exists, Object> {

  private final List<CrudRepository> repositories;
  private CrudRepository repository;

  public ExistsValidator(final List<CrudRepository> repositories) {
    this.repositories = repositories;
  }

  @Override
  public void initialize(final Exists exists) {
    this.repository = repositories.stream().filter(it -> ResolvableType.forClass(it.getClass())
        .as(CrudRepository.class).getGeneric(0).resolve().equals(exists.value()))
        .findFirst().orElseThrow(() -> new RuntimeException("Can't find the repository for the document"));
  }

  @Override
  public boolean isValid(final Object id, final ConstraintValidatorContext cxt) {
    Assert.isTrue( id instanceof Iterable || id.getClass().equals(repositoryIdClass()),
        String.format("%s is not the expected id type of %s", id.getClass(), repositoryIdClass()));
    return id == null
        || !(id instanceof Iterable)
        ? repository.existsById(id)
        : existsIterableIds((Iterable)id);
  }

  private boolean existsIterableIds(Iterable<?> ids) {
    final long expectedSize = StreamSupport.stream(ids.spliterator(), false).count();
    return StreamSupport.stream(
        repository.findAllById(ids).spliterator(), false).count() == expectedSize;
  }

  private Class<?> repositoryIdClass() {
    return ResolvableType.forClass(repository.getClass())
        .as(CrudRepository.class).getGeneric(1).resolve();
  }

}
