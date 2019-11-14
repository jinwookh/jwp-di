package nextstep.di.factory.strategy;

import nextstep.annotation.Configuration;
import nextstep.di.factory.BeanFactoryUtils;
import nextstep.di.factory.exception.InaccessibleConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstructorBeanCreationStrategy implements  BeanCreationStrategy{
    private final Set<Class<?>> preInstantiateBeans;
    private Set<Class<?>> preConstructorInstantiateBeans;


    public ConstructorBeanCreationStrategy(Set<Class<?>> preConstructorInstantiateBeans) {
        this.preConstructorInstantiateBeans = preConstructorInstantiateBeans;
        this.preInstantiateBeans = getMethodBeanClasses();
        preInstantiateBeans.addAll(preConstructorInstantiateBeans);

    }

    private Set<Class<?>> getMethodBeanClasses() {
            Set<Class<?>> configurationBeans = preConstructorInstantiateBeans.stream()
                    .filter(key -> key.isAnnotationPresent(Configuration.class))
                    .collect(Collectors.toSet());

            return configurationBeans.stream()
                    .map(Class::getDeclaredMethods)
                    .flatMap(Arrays::stream)
                    .map(Method::getReturnType)
                    .collect(Collectors.toSet());

    }


    @Override
    public boolean canHandle(Class<?> clazz) {
        return preConstructorInstantiateBeans.contains(clazz);
    }

    @Override
    public List<Class<?>> getDependencyTypes(Class<?> clazz) {
        clazz = BeanFactoryUtils.findConcreteClass(clazz, preConstructorInstantiateBeans);
        Constructor<?> constructor = getConstructor(clazz);
        return Arrays.asList(constructor.getParameterTypes());
    }

    private Constructor<?> getConstructor(Class<?> beanClass) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(beanClass);
        if (constructor != null) {
            return constructor;
        }

        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        constructor = BeanFactoryUtils
                .findBeansConstructor(constructors, preInstantiateBeans)
                .or(() -> BeanFactoryUtils.findDefaultConstructor(constructors))
                .orElseThrow(InaccessibleConstructorException::new);

        return constructor;
    }

    @Override
    public Object createBean(Class<?> clazz, List<Object> parameterInstances,  Map<Class<?>, Object> beans) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        clazz = BeanFactoryUtils.findConcreteClass(clazz, preConstructorInstantiateBeans);
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }
        Constructor<?> constructor = getConstructor(clazz);
        Object instance = constructor.newInstance(parameterInstances.toArray());

        beans.put(clazz, instance);
        return instance;
    }
}
