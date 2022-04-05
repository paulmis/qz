package client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import commons.entities.messages.SSEMessageType;
import commons.entities.utils.Multiplier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * The tests for the helper methods
 * found inside the ReflectionUtils class.
 */
public class ReflectionUtilsTest {

    @AllArgsConstructor
    class Person implements SSESource {
        @Description("name")
        public String fullName;

        @Description("age in years")
        @DecimalMax("160")
        @DecimalMin("0")
        @Multiplier(value =  2)
        public Integer age;

        @Description("height in centimeters")
        @DecimalMax("300")
        @DecimalMin("0")
        public Float height;

        @Description("balance in euros")
        public Float balance;
        public Integer brothers;

        public void bindHandler(SSEHandler handler) {
            handler.initialize(this);
        }

        @SSEEventHandler(SSEMessageType.INIT)
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
        var annotatedMethodsName = ReflectionUtils.getAnnotatedMethods(personObj, SSEEventHandler.class)
                .stream().map(Method::getName).collect(Collectors.toList());

        assertEquals(List.of("getBalance"), annotatedMethodsName);
    }

    @Test
    void getTextName() throws NoSuchMethodException {
        var getBalanceMethod = personObj.getClass().getMethod("getBalance");
        var otherMethod = personObj.getClass().getMethod("otherFunction");

        assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.getSSEEventName(otherMethod));
        assertEquals(SSEMessageType.INIT, ReflectionUtils.getSSEEventName(getBalanceMethod));
    }

    @Test
    void getMultiplierValue() throws NoSuchFieldException {
        var ageField = personObj.getClass().getField("age");
        var balanceField = personObj.getClass().getField("balance");

        assertEquals(1, ReflectionUtils.getMultiplierValue(balanceField));
        assertEquals(2, ReflectionUtils.getMultiplierValue(ageField));
    }

}