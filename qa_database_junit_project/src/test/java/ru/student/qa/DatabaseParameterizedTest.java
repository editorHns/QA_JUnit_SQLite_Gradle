package ru.student.qa;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(QaResultExtension.class)
class DatabaseParameterizedTest {
    static final String TEST_SET="chess-counting";

    @BeforeAll static void initDb(){ ResultDatabase.initialize(); }

    static Stream<Arguments> databaseCases(){
        return ResultDatabase.loadTestCases(TEST_SET).stream()
                .map(t -> Arguments.of(Double.parseDouble(t.input()), t.expected()));
    }

    @ParameterizedTest(name="[{index}] square({0}) = {1}")
    @MethodSource("databaseCases")
    void squareFromDatabase(double input,double expected){
        assertEquals(expected, Calculator.square(input), 0.000001);
    }

    @Test void divisionWorks(){ assertEquals(5.0,Calculator.safeDivide(10,2),0.000001); }
    @Test void divisionByZeroThrows(){ Assertions.assertThrows(IllegalArgumentException.class,()->Calculator.safeDivide(10,0)); }

    @Test void intentionallyFailingDemo(){
        Assumptions.assumeTrue(Boolean.parseBoolean(System.getProperty("demoFailure","false")),"Демо-ошибка выключена");
        assertEquals(5,2+2,"Намеренно неверный тест");
    }
}
