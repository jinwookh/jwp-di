package nextstep.di.factory;

import nextstep.di.factory.example.Car;
import nextstep.di.factory.example.JdbcUserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructorReflectionTest {
    private static final Logger log = LoggerFactory.getLogger(ConstructorReflectionTest.class);

    @Test
    void checkEmptyConstructorTest() {
        assertThat(JdbcUserRepository.class.getDeclaredConstructors().length).isEqualTo(1);
        assertThat(JdbcUserRepository.class.getDeclaredConstructors()[0].getParameterTypes().length).isEqualTo(0);
    }

    @Test
    void primitiveIntConstructorTest() {
        for (Class<?> parameterType : Car.class.getConstructors()[0].getParameterTypes()) {
            log.info("{}");
        }
    }
}
