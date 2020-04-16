package io.github.manuelarte.spring.manuelartevalidation;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.manuelarte.spring.manuelartevalidation.ManuelarteValidationApplicationTests.ParentDocumentController;
import io.github.manuelarte.spring.manuelartevalidation.ManuelarteValidationApplicationTests.ParentRepository;
import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.NestedServletException;

@SpringBootTest(classes = ManuelarteValidationApplicationTests.class)
@ContextConfiguration(classes = ManuelarteValidationApplicationTests.class)
@Import( { ParentRepository.class, ParentDocumentController.class} )
@EnableWebMvc
@AutoConfigureMockMvc
@EnableAutoConfiguration
@EnableMongoRepositories
class ManuelarteValidationApplicationTests {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MockMvc mvc;

	@AfterEach
	public void tearDown() {
		mongoTemplate.dropCollection(ParentDocument.class);
	}

	@Test
	void testExistWithOneId() throws Exception {
		final ParentDocument saved = mongoTemplate.save(new ParentDocument());
		mvc.perform(get("/api/parents/{id}", saved.id)
				.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(saved.id));
	}

	@Test
	void testExistWithNotExisting() {
		final NestedServletException e = assertThrows(NestedServletException.class,
				() -> mvc.perform(get("/api/parents/{id}", new ObjectId().toString())
				.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest()));
		assertEquals(ConstraintViolationException.class, e.getCause().getClass());
	}

	@Test
	void testExistWithSeveralIds() throws Exception {
		final ParentDocument saved = mongoTemplate.save(new ParentDocument());
		final ParentDocument saved2 = mongoTemplate.save(new ParentDocument());
		final String ids = Arrays.asList(saved, saved2).stream().map(it -> it.id.toString())
				.collect(Collectors.joining(", "));
		mvc.perform(get("/api/parents?ids={ids}", ids)
				.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void testExistWithSeveralIdsOneNotExisting() {
		final ParentDocument saved = mongoTemplate.save(new ParentDocument());
		final ParentDocument saved2 = new ParentDocument();
		saved2.id = new ObjectId().toString();
		final String ids = Arrays.asList(saved, saved2).stream().map(it -> it.id)
				.collect(Collectors.joining(", "));
		final Exception e = assertThrows(NestedServletException.class,
				() -> mvc.perform(get("/api/parents?ids={ids}", ids)
				.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest()));
		assertEquals(ConstraintViolationException.class, e.getCause().getClass());
	}

	@RestController
	@RequestMapping("/api/parents")
	@Validated
	public static class ParentDocumentController {

		private final MongoTemplate mongoTemplate;

		public ParentDocumentController(final MongoTemplate mongoTemplate) {
			this.mongoTemplate = mongoTemplate;
		}

		@GetMapping
		public List<ParentDocument> findAllById(@RequestParam @Exists(ParentDocument.class) final List<String> ids) {
			return mongoTemplate.find(new Query(Criteria.where("_id").in(ids)), ParentDocument.class);
		}

		@GetMapping("/{id}")
		public ParentDocument findOne(@PathVariable @Exists(ParentDocument.class) final String id) {
			return mongoTemplate.findById(id, ParentDocument.class);
		}
	}

	@Component
	public static class ParentRepository implements CrudRepository<ParentDocument, String>{

		@Autowired
		private MongoTemplate mongoTemplate;

		@Override
		public <S extends ParentDocument> S save(S entity) {
			return mongoTemplate.save(entity);
		}

		@Override
		public <S extends ParentDocument> Iterable<S> saveAll(Iterable<S> entities) {
			return null;
		}

		@Override
		public Optional<ParentDocument> findById(String id) {
			return Optional.ofNullable(mongoTemplate.findById(id, ParentDocument.class));
		}

		@Override
		public boolean existsById(String id) {
			return findById(id).isPresent();
		}

		@Override
		public Iterable<ParentDocument> findAll() {
			return mongoTemplate.findAll(ParentDocument.class);
		}

		@Override
		public Iterable<ParentDocument> findAllById(final Iterable<String> ids) {
			final List<String> listIds = StreamSupport.stream(
					ids.spliterator(), false).collect(Collectors.toList());
			return mongoTemplate.find(
					new Query(Criteria.where("_id").in(listIds.toArray(new String[listIds.size()]))),
					ParentDocument.class);
		}

		@Override
		public long count() {
			return StreamSupport.stream(findAll().spliterator(), false).count();
		}

		@Override
		public void deleteById(String aLong) {
		}

		@Override
		public void delete(ParentDocument entity) {

		}

		@Override
		public void deleteAll(Iterable<? extends ParentDocument> entities) {

		}

		@Override
		public void deleteAll() {

		}
	}

	@Document
	public static class ParentDocument {

		@Id
		private String id;

		public String getId() {
			return id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ParentDocument that = (ParentDocument) o;
			return Objects.equals(id, that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
	}

}
