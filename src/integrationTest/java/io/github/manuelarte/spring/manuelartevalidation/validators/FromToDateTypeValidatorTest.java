package io.github.manuelarte.spring.manuelartevalidation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.manuelarte.spring.manuelartevalidation.constraints.FromToDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.fromto.FromDate;
import io.github.manuelarte.spring.manuelartevalidation.constraints.fromto.ToDate;
import java.time.YearMonth;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.annotation.Validated;

@SpringBootTest(classes = FromToDateTypeValidatorTest.class)
@ContextConfiguration(classes = FromToDateTypeValidatorTest.class)
@EnableAutoConfiguration
class FromToDateTypeValidatorTest {

	@Autowired
	private Validator validator;

	@Test
	@SuppressWarnings("JavaTimeDefaultTimeZone")
	void testFromToButToOTherIsInvalid() {
		final EntityExample example = new EntityExample(YearMonth.now(), YearMonth.now().plusYears(1),
				YearMonth.now().minusMonths(1));
		assertFalse(validator.validate(example).isEmpty());
	}

	@Test
	@SuppressWarnings("JavaTimeDefaultTimeZone")
	void testFromToFromOtherIsInvalid() {
		final EntityExample example = new EntityExample(YearMonth.now(), YearMonth.now().plusYears(1),
				YearMonth.now().plusMonths(1));
		assertTrue(validator.validate(example).isEmpty());
	}

	@Validated
	@FromToDate
	@lombok.AllArgsConstructor
	@lombok.Data
	public static class EntityExample {

		@FromDate
		@FromDate("other")
		private final YearMonth from;
		@ToDate
		private final YearMonth to;

		@ToDate("other")
		private final YearMonth otherTo;

	}

}
