package proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DynamicInvocationHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(DynamicInvocationHandler.class);
    private final Map<String, Method> methods = new HashMap();
    private Object target;

    public DynamicInvocationHandler(Object target) {
        this.target = target;
        for (Method method : target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = methods.get(method.getName()).invoke(target, args);

        return ((String) result).toUpperCase();
    }
}
