package proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {

    @Test
    void jdk_proxy_text() {
        Hello proxyHello = (Hello) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(),
                new Class[]{Hello.class}, new DynamicInvocationHandler(new HelloImpl()));

        assertThat(proxyHello.sayHello("harry")).isEqualTo("HELLO HARRY");
    }
}
