package nextstep.di.factory;

import com.google.common.collect.Maps;
import nextstep.annotation.Configuration;
import nextstep.di.factory.exception.CycleReferenceException;
import nextstep.di.factory.strategy.BeanCreationStrategies;
import nextstep.di.factory.strategy.ConstructorBeanCreationStrategy;
import nextstep.di.factory.strategy.MethodBeanCreationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preConstructInstantiateBeans;
    private Set<Class<?>> preMethodInstantiateBeans;

    private BeanCreationStrategies beanCreationStrategies;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preConstructInstantiateBeans) {
        this.preConstructInstantiateBeans = Collections.unmodifiableSet(preConstructInstantiateBeans);
        log.debug("pre construction beans: {}", preConstructInstantiateBeans);
        this.preMethodInstantiateBeans = Collections.unmodifiableSet(makePreMethodInstantiateBeans());
        log.debug("pre method beans: {}", preMethodInstantiateBeans);
        this.beanCreationStrategies = makeCreationStrategies();
    }

    private Set<Class<?>> makePreMethodInstantiateBeans() {
        return BeanFactoryUtils.getMethodBeanClasses(preConstructInstantiateBeans);
    }

    private Set<Method> getMethods(Set<Class<?>> preConstructInstantiateBeans) {
        Set<Class<?>> configurationBeans = preConstructInstantiateBeans.stream()
                .filter(key -> key.isAnnotationPresent(Configuration.class))
                .collect(Collectors.toSet());

        return configurationBeans.stream()
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    private BeanCreationStrategies makeCreationStrategies() {
        BeanCreationStrategies beanCreationStrategies = new BeanCreationStrategies(
                Arrays.asList(new ConstructorBeanCreationStrategy(preConstructInstantiateBeans), new MethodBeanCreationStrategy(getMethods(preConstructInstantiateBeans), preMethodInstantiateBeans)));

        return beanCreationStrategies;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
//        checkCycleReference(preConstructInstantiateBeans);
        preConstructInstantiateBeans
                .forEach(preConstructInstiateBean -> createBean(preConstructInstiateBean));
        preMethodInstantiateBeans
                .forEach(preMethodInstantiateBean -> createBean(preMethodInstantiateBean));
    }

    private void checkCycleReference(Set<Class<?>> preInstantiateBeans) {
        preInstantiateBeans.forEach(beanClass -> checkCycleReference(beanClass, new ArrayList<>()));
    }

    private void checkCycleReference(Class<?> beanClass, List<String> beanNames) {
        if (beanClass.isInterface()) {
            return;
        }
        log.debug("checking cycle reference: {}", beanClass);
        beanClass = BeanFactoryUtils.findConcreteClass(beanClass, preConstructInstantiateBeans);
        Constructor<?> constructor = BeanFactoryUtils.getConstructor(beanClass, preConstructInstantiateBeans);
        if (beanNames.contains(constructor.getName())) {
            throw new CycleReferenceException();
        }
        beanNames.add(constructor.getName());
        Arrays.stream(constructor.getParameterTypes()).forEach(paramterBeanClass -> checkCycleReference(paramterBeanClass, beanNames));
    }


    private void createBean(Class<?> preInstantiateBean) {
        beanCreationStrategies.createBean(preInstantiateBean, beans);
    }

    public Map<Class<?>, Object> getBeans() {
        return Collections.unmodifiableMap(beans);
    }
}
