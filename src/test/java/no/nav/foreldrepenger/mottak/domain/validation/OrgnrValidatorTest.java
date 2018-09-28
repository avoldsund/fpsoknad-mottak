package no.nav.foreldrepenger.mottak.domain.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.BeforeClass;
import org.junit.Test;

public class OrgnrValidatorTest {

    private static final String NAV = "999263550";
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testOK() {
        assertTrue(validator.validate(new TestClass(NAV)).isEmpty());
    }

    @Test
    public void testOKbutWrongFirstDigit() {
        assertFalse(validator.validate(new TestClass("123456785")).isEmpty());
    }

    @Test
    public void testNull() {
        assertFalse(validator.validate(new TestClass(null)).isEmpty());
    }

    @Test
    public void testLength() {
        assertFalse(validator.validate(new TestClass("666")).isEmpty());
    }

    static class TestClass {

        @Orgnr
        private final String orgnr;

        public TestClass(String orgnr) {
            this.orgnr = orgnr;
        }

    }
}