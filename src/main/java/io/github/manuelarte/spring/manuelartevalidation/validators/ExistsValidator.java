package io.github.manuelarte.spring.manuelartevalidation.validators;

import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@lombok.RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, Object> {

  @lombok.NonNull
  private final List<CrudRepository<?, ?>> repositories;
  private CrudRepository<Object, Object> repository;

  @Override
  public void initialize(@NotNull final Exists exists) {
    this.repository = (CrudRepository<Object, Object>) repositories.stream().filter(it -> Objects
        .requireNonNull(ResolvableType.forClass(it.getClass())
            .as(CrudRepository.class).getGeneric(0).resolve()).equals(exists.value()))
        .findFirst().orElseThrow(() -> new RuntimeException("Can't find the repository for the entity"));
  }

  @Override
  public boolean isValid(final Object id, final ConstraintValidatorContext cxt) {
    Assert.isTrue( id == null
            || id instanceof Iterable
            || id.getClass().equals(repositoryIdClass()),
        String.format("%s is not the expected id type of %s",
            Optional.ofNullable(id).map(Object::getClass), repositoryIdClass()));
    return id == null
        || (!(id instanceof Iterable)
        ? repository.existsById(id)
        : existsIterableIds((Iterable<Object>)id));
  }

  private boolean existsIterableIds(final Iterable<Object> ids) {
    final long expectedSize = StreamSupport.stream(ids.spliterator(), false).count();
    return StreamSupport.stream(
        repository.findAllById(ids).spliterator(), false).count() == expectedSize;
  }

  private Class<?> repositoryIdClass() {
    return ResolvableType.forClass(repository.getClass())
        .as(CrudRepository.class).getGeneric(1).resolve();
  }

}
