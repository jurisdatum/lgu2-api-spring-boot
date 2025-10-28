package uk.gov.legislation.data.marklogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SuppressWarnings("unused")
class AbstractParametersTest {

    /**
     * This is a test class for the AbstractParameters class.
     * The purpose of this test is to validate the behavior of the toQuery() method,
     * which generates a query string representation of the object's fields.
     */

    @Test
    void testToQuery_withMultipleFields() {
        class TestParameters extends AbstractParameters {
            final String fieldOne = "value1";

            final String fieldTwo = "value2";
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("?field-one=value1&field-two=value2", query);
    }


    @Test
    void testToQuery_withNoFields() {
        class TestParameters extends AbstractParameters {
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("", query);
    }

    @Test
    void testToQuery_withNullField() {
        class TestParameters extends AbstractParameters {
            final String fieldOne = "value1";
            final String fieldTwo = null;
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("?field-one=value1", query);
    }

    @Test
    void testToQuery_withEncodedCharacters() {
        class TestParameters extends AbstractParameters {
            final String fieldWithSpaces = "test value";
            final String specialCharacters = "key=value&other";
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("?field-with-spaces=test+value&special-characters=key%3Dvalue%26other", query);
    }

    @Test
    void testToQuery_withStaticField() {
        class TestParameters extends AbstractParameters {
            static final String staticField = "static";
            final String field = "value";
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("?field=value", query);
    }


    @Test
    void testToQuery_withEmptyStringField() {
        class TestParameters extends AbstractParameters {
            final String field = "";
        }

        TestParameters parameters = new TestParameters();
        String query = parameters.toQuery();

        assertEquals("?field=", query);
    }

}