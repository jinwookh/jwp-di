package proxy;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {

    @Test
    void jdk_proxy_text() {
        Hello proxyHello = getJdkDynamicProxy();

        assertThat(proxyHello.sayHello("harry")).isEqualTo("HELLO HARRY");
    }

    private Hello getJdkDynamicProxy() {
        return (Hello) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(),
                new Class[]{Hello.class}, new DynamicInvocationHandler(new HelloImpl()));
    }

    @Test
    void cglib_proxy_test() {
        Hello proxyHello = getCglibProxy();

        assertThat(proxyHello.sayHello("harry")).isEqualTo("HELLO HARRY");
    }

    private Hello getCglibProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloImpl.class);
        enhancer.setCallback(new UpperCaseInterceptor());
        Object obj = enhancer.create();

        return (Hello) obj;
    }
}
