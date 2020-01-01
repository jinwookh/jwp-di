package proxy;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest {
    private static final Logger log = LoggerFactory.getLogger(ProxyTest.class);

    @Test
    void jdk_proxy_text() {
        Hello proxyHello = getJdkDynamicHelloProxy();

        assertThat(proxyHello.sayHello("harry")).isEqualTo("HELLO HARRY");
    }

    private Hello getJdkDynamicHelloProxy() {
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

    /*
    몇 번 측정해 보았지만 결과가 매 번 다르다.
    아래 코드로는 어느 게 더 빠른지 알기 어려움.
     */
    @Test
    void cglib_jdkDynamic_시간_비교() {

        StopWatch secondSw = new StopWatch();
        measureProxyTime(secondSw, getJdkDynamicHelloProxy());
        log.info("JdkDynamic proxy 시간 : {}", secondSw.getTotalTimeSeconds());

        StopWatch sw = new StopWatch();
        measureProxyTime(sw, getCglibProxy());
        log.info("Cglib proxy 시간 : {}", sw.getTotalTimeSeconds());

    }

    private void measureProxyTime(StopWatch sw, Hello jdkDynamicHelloProxy) {
        sw.start();
        for (int i = 0; i < 10000000; i++) {
            Hello hello = jdkDynamicHelloProxy;
            hello.sayHello("harry");
        }

        sw.stop();
    }
}
