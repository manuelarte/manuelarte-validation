package io.github.manuelarte.spring.manuelartevalidation.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.ReflectionUtils;

/**
 * {@inheritDoc}
 */
@NoRepositoryBean
public interface CrpudRepository<T, Id> extends CrudRepository<T, Id> {

  /**
   * To partially update an entity. Updates the non-null fields of the previously saved entity.
   * Prerequisites. The entity
   * @param id Id of the entity to be partially updated.
   * @param partialEntity the entity with the fields that are going to be updated.
   *                      The entity needs to have an @Id annotation in the field
   * @return The entity updated with the non-null partial entity values
   */
  default T partialSave(Id id, T partialEntity) {
    final List<Field> allFields = new ArrayList<>();
    ReflectionUtils.doWithFields(partialEntity.getClass(), allFields::add);
    final T saved = findById(id)
        .orElseThrow(() -> new RuntimeException(String.format("Entity with id %s not found", id)));
    BeanUtils.copyProperties(partialEntity, saved, allFields.stream()
        .filter(it -> {
          try {
            it.setAccessible(true);
            return it.get(partialEntity) == null;
          } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't retrieve value from " + it.getName());
          }
        }).map(Field::getName).toArray(String[]::new));
    return save(saved);
  }

}
