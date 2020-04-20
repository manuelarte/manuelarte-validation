package io.github.manuelarte.spring.manuelartevalidation.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate;
import io.github.manuelarte.spring.manuelartevalidation.validators.FromToDateCrossParameterValidatorTest.TestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.ConstraintTarget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.NestedServletException;

@SpringBootTest(classes = FromToDateCrossParameterValidatorTest.class)
@ContextConfiguration(classes = FromToDateCrossParameterValidatorTest.class)
@Import(TestController.class)
@EnableWebMvc
@AutoConfigureMockMvc
@EnableAutoConfiguration
class FromToDateCrossParameterValidatorTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void testFromToSameDateAndIsInvalid() {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		final Date sameDate = new Date();
		assertThrows(NestedServletException.class, () -> mvc.perform(get("/api/fromto?from={from}&to={to}",
				formatter.format(sameDate),
				formatter.format(sameDate))));
	}

	@Test
	void testFromToDifferentDateAndIsValid() throws Exception {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		final Date from = new Date();
		final Date to = new Date(from.getTime() + 2*24*60*60*1000);
		mvc.perform(get("/api/fromto?from={from}&to={to}",
				formatter.format(from),
				formatter.format(to)));
	}

	@RestController
	@RequestMapping("/api/fromto")
	@Validated
	public static class TestController {

		@GetMapping
		@FromToDate(validationAppliesTo = ConstraintTarget.PARAMETERS)
		public ResponseEntity<Void> findAllById(
				@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date from,
				@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date to) {
			return null;
		}

	}

}
