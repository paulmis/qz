package client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import jdk.jfr.Description;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The tests for the helper methods
 * found inside the ReflectionUtils class.
 */
public class ReflectionUtilsTest {

    @AllArgsConstructor
    class Person {
        @Description("name")
        public String fullName;

        @Description("age in years")
        @DecimalMax("160")
        @DecimalMin("0")
        public Integer age;

        @Description("height in centimeters")
        @DecimalMax("300")
        @DecimalMin("0")
        public Float height;

        @Description("balance in euros")
        public Float balance;
        public Integer brothers;

        @Name("balance")
        public void getBalance() {

        }

        public void otherFunction() {

        }
    }

    Person personObj;

    @BeforeEach
    void createPersonObject() {
        personObj =
                new Person("Andy Zaidman", 69,  189f, (float) (1 << 20), 0);
    }

    @Test
    void getAnnotatedFields() {
        var annotatedFieldsName = ReflectionUtils.getAnnotatedFields(personObj, Description.class)
                .stream().map(Field::getName).collect(Collectors.toList());

        assertEquals(List.of("fullName", "age", "height", "balance"), annotatedFieldsName);
    }

    @Test
    void getTextDescription() throws NoSuchFieldException {
        var fullNameField = personObj.getClass().getField("fullName");
        var brotherField = personObj.getClass().getField("brothers");

        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.getTextDescription(brotherField));
        assertEquals("name", ReflectionUtils.getTextDescription(fullNameField));
    }

    @Test
    void getMinValue() throws NoSuchFieldException {
        var ageField = personObj.getClass().getField("age");
        var balanceField = personObj.getClass().getField("balance");

        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.getMinValue(balanceField));
        assertEquals(0, ReflectionUtils.getMinValue(ageField));
    }

    @Test
    void getMaxValue() throws NoSuchFieldException {
        var ageField = personObj.getClass().getField("age");
        var balanceField = personObj.getClass().getField("balance");

        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.getMaxValue(balanceField));
        assertEquals(160, ReflectionUtils.getMaxValue(ageField));
    }

    @Test
    void getAnnotatedMethods() {
        var annotatedMethodsName = ReflectionUtils.getAnnotatedMethods(personObj, Name.class)
                .stream().map(Method::getName).collect(Collectors.toList());

        assertEquals(List.of("getBalance"), annotatedMethodsName);
    }

    @Test
    void getTextName() throws NoSuchMethodException {
        var getBalanceMethod = personObj.getClass().getMethod("getBalance");
        var otherMethod = personObj.getClass().getMethod("otherFunction");

        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.getTextName(otherMethod));
        assertEquals("balance", ReflectionUtils.getTextName(getBalanceMethod));
    }

}