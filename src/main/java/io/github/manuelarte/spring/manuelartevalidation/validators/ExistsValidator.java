package io.github.manuelarte.spring.manuelartevalidation.validators;

import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExistsValidator implements ConstraintValidator<Exists, Object> {

  private final MongoTemplate mongoTemplate;
  private Class<?> document;

  public ExistsValidator(final MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void initialize(final Exists exists) {
    this.document = exists.value();
  }

  @Override
  public boolean isValid(final Object id, final ConstraintValidatorContext cxt) {
    return id == null
        || !(id instanceof Iterable)
        ? mongoTemplate.exists(new Query(Criteria.where("_id").is(id)), document)
        : existsIterableIds((Iterable)id);
  }

  private boolean existsIterableIds(Iterable<?> ids) {
    final List<Criteria> criteriaList = StreamSupport.stream(ids.spliterator(), false)
        .map(id -> Criteria.where("_id").is(id)).collect(Collectors.toList());
    final Criteria[] orCriteria = criteriaList.toArray(new Criteria[criteriaList.size()]);
    final Query allIdsQuery = new Query(new Criteria().orOperator(orCriteria));
    return mongoTemplate.count(allIdsQuery, document) == criteriaList.size();
  }

}
