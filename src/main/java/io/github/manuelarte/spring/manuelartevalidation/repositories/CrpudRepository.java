package io.github.manuelarte.spring.manuelartevalidation.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.ReflectionUtils;

/**
 * Repository that also allows partial updates
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface CrpudRepository<T, ID> extends CrudRepository<T, ID> {

  /**
   * To partially update an entity. Updates the non-null fields of the previously saved entity.
   * Prerequisites. The entity
   * @param id Id of the entity to be partially updated.
   * @param partialEntity the entity with the fields that are going to be updated.
   *                      The entity needs to have an @Id annotation in the field
   * @return
   */
  default T partialSave(ID id, T partialEntity) {
    final List<Field> allFields = new ArrayList<>();
    ReflectionUtils.doWithFields(partialEntity.getClass(), it -> allFields.add(it));
    final List<String> ignoreNull = allFields.stream()
        .filter(it -> {
          try {
            it.setAccessible(true);
            return it.get(partialEntity) == null;
          } catch (IllegalAccessException e) {
            throw new RuntimeException("Can't retrieve value from " + it.getName());
          }
        }).map(it -> it.getName())
        .collect(Collectors.toList());
    final T saved = findById(id)
        .orElseThrow(() -> new RuntimeException(String.format("Entity with id %s not found", id)));
    BeanUtils.copyProperties(partialEntity, saved, ignoreNull.toArray(new String[ignoreNull.size()]));
    return save(saved);
  }

}
