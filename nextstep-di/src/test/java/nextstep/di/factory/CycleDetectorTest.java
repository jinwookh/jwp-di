package nextstep.di.factory;

import nextstep.di.factory.example.CycleTestController;
import nextstep.di.factory.example.CycleTestService;
import nextstep.di.factory.exception.CycleException;
import nextstep.stereotype.Controller;
import nextstep.stereotype.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CycleDetectorTest {
    private Set<Class<?>> clazzes;

    @BeforeEach
    void setup() {
        BeanScanner beanScanner = new BeanScanner("nextstep.di.factory.example");
        clazzes = beanScanner.getTypesAnnotatedWith(Controller.class, Service.class);
    }

    @Test
    void 순환참조() {
        CycleDetector cycleDetector = new CycleDetector(clazzes);

        assertThrows(CycleException.class, () -> {
            cycleDetector.detect();
        });
    }

    @Test
    void 순환참조아님() {
        Set<Class<?>> clazzesWithoutCycle = new HashSet<>(clazzes);
        clazzesWithoutCycle.remove(CycleTestController.class);
        clazzesWithoutCycle.remove(CycleTestService.class);

        CycleDetector cycleDetector = new CycleDetector(clazzesWithoutCycle);
        assertDoesNotThrow(() -> {
            cycleDetector.detect();
        });

    }

}