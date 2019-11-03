package nextstep.di.factory;

import nextstep.di.factory.exception.CycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

public class CycleDetector {
    private static final Logger log = LoggerFactory.getLogger(CycleDetector.class);

    private final Set<Class<?>> clazzes;

    public CycleDetector(Set<Class<?>> clazzes) {

        this.clazzes = clazzes;
    }

    public boolean detect() {
        for (Class<?> clazz : clazzes) {
            noCycle(clazz, new HashSet<>());
        }
        return true;

    }

    private boolean noCycle(Class<?> clazz, Set<Class<?>> predecessors) {
        Constructor<?>[] constructors = clazz.getConstructors();

        for (Constructor<?> constructor : constructors) {
            checkConstructor(predecessors, constructor);
        }
        return true;
    }

    private void checkConstructor(Set<Class<?>> predecessors, Constructor<?> constructor) {
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            if (predecessors.contains(parameterType)) {
                throw new CycleException();
            }
            if (!parameterType.isPrimitive()) {
                Set<Class<?>> addedPredecessors = new HashSet<>(predecessors);
                addedPredecessors.add(parameterType);
                noCycle(parameterType, addedPredecessors);
            }
        }
    }
}
