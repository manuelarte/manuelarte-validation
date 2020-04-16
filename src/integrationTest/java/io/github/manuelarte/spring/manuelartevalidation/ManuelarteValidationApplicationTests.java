package io.github.manuelarte.spring.manuelartevalidation;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.manuelarte.spring.manuelartevalidation.ManuelarteValidationApplicationTests.ParentDocumentController;
import io.github.manuelarte.spring.manuelartevalidation.constraints.Exists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
@Import(ParentDocumentController.class)
@EnableWebMvc
@AutoConfigureMockMvc
@EnableAutoConfiguration
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
		mvc.perform(get("/api/parents/{id}", saved.id.toString())
				.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(saved.id.toString()));
	}

	@Test
	void testExistWithNotExisting() {
		final NestedServletException e = assertThrows(NestedServletException.class, () -> mvc.perform(get("/api/parents/{id}", new ObjectId().toString())
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
	void testExistWithSeveralIdsOneNotExisting() throws Exception {
		final ParentDocument saved = mongoTemplate.save(new ParentDocument());
		final ParentDocument saved2 = new ParentDocument();
		saved2.id = new ObjectId();
		final String ids = Arrays.asList(saved, saved2).stream().map(it -> it.id.toString())
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
			return mongoTemplate.findById(new ObjectId(id), ParentDocument.class);
		}
	}

	@Document
	public static class ParentDocument {

		@Id
		@JsonSerialize(using = ToStringSerializer.class)
		private ObjectId id;

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
