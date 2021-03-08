import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

public class A$$EnhancerBySpringCGLIB$$21771092 extends A implements SpringProxy, Advised, Factory {
    private boolean CGLIB$BOUND;
    public static Object CGLIB$FACTORY_DATA;
    private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
    private static final Callback[] CGLIB$STATIC_CALLBACKS;
    private MethodInterceptor CGLIB$CALLBACK_0;
    private MethodInterceptor CGLIB$CALLBACK_1;
    private NoOp CGLIB$CALLBACK_2;
    private Dispatcher CGLIB$CALLBACK_3;
    private Dispatcher CGLIB$CALLBACK_4;
    private MethodInterceptor CGLIB$CALLBACK_5;
    private MethodInterceptor CGLIB$CALLBACK_6;
    private static Object CGLIB$CALLBACK_FILTER;
    private static final Method CGLIB$test$0$Method;
    private static final MethodProxy CGLIB$test$0$Proxy;
    private static final Object[] CGLIB$emptyArgs;
    private static final Method CGLIB$equals$1$Method;
    private static final MethodProxy CGLIB$equals$1$Proxy;
    private static final Method CGLIB$toString$2$Method;
    private static final MethodProxy CGLIB$toString$2$Proxy;
    private static final Method CGLIB$hashCode$3$Method;
    private static final MethodProxy CGLIB$hashCode$3$Proxy;
    private static final Method CGLIB$clone$4$Method;
    private static final MethodProxy CGLIB$clone$4$Proxy;

    public A$$EnhancerBySpringCGLIB$$21771092() {
        CGLIB$BIND_CALLBACKS(this);
    }

    static {
        CGLIB$STATICHOOK29();
    }

    public final boolean equals(Object var1) {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_5;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_5;
        }

        if (var10000 != null) {
            Object var2 = var10000.intercept(this, CGLIB$equals$1$Method, new Object[]{var1}, CGLIB$equals$1$Proxy);
            return var2 == null ? false : (Boolean)var2;
        } else {
            return super.equals(var1);
        }
    }

    public final String toString() {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_0;
        }

        return var10000 != null ? (String)var10000.intercept(this, CGLIB$toString$2$Method, CGLIB$emptyArgs, CGLIB$toString$2$Proxy) : super.toString();
    }

    public final int hashCode() {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_6;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_6;
        }

        if (var10000 != null) {
            Object var1 = var10000.intercept(this, CGLIB$hashCode$3$Method, CGLIB$emptyArgs, CGLIB$hashCode$3$Proxy);
            return var1 == null ? 0 : ((Number)var1).intValue();
        } else {
            return super.hashCode();
        }
    }

    protected final Object clone() throws CloneNotSupportedException {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_0;
        }

        return var10000 != null ? var10000.intercept(this, CGLIB$clone$4$Method, CGLIB$emptyArgs, CGLIB$clone$4$Proxy) : super.clone();
    }

    public final int indexOf(Advice var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).indexOf(var1);
    }

    public final int indexOf(Advisor var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).indexOf(var1);
    }

    public Object newInstance(Callback var1) {
        throw new IllegalStateException("More than one callback object required");
    }

    public Object newInstance(Class[] var1, Object[] var2, Callback[] var3) {
        CGLIB$SET_THREAD_CALLBACKS(var3);
        A$$EnhancerBySpringCGLIB$$21771092 var10000 = new A$$EnhancerBySpringCGLIB$$21771092;
        switch(var1.length) {
        case 0:
            var10000.<init>();
            CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
            return var10000;
        default:
            throw new IllegalArgumentException("Constructor not found");
        }
    }

    public Object newInstance(Callback[] var1) {
        CGLIB$SET_THREAD_CALLBACKS(var1);
        A$$EnhancerBySpringCGLIB$$21771092 var10000 = new A$$EnhancerBySpringCGLIB$$21771092();
        CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
        return var10000;
    }

    public final boolean isFrozen() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).isFrozen();
    }

    // 被代理的目标方法
    public final void test() {
        // 获取MethodInterceptor
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
        if (var10000 == null) {
            // 获取MethodInterceptor
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_0;
        }

        // 此时的实现是org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor
        if (var10000 != null) {
            // 调用intercept
            var10000.intercept(this, CGLIB$test$0$Method, CGLIB$emptyArgs, CGLIB$test$0$Proxy);
        } else {
            super.test();
        }
    }

    public static MethodProxy CGLIB$findMethodProxy(Signature var0) {
        String var10000 = var0.toString();
        switch(var10000.hashCode()) {
        case -1422510685:
            if (var10000.equals("test()V")) {
                return CGLIB$test$0$Proxy;
            }
            break;
        case -508378822:
            if (var10000.equals("clone()Ljava/lang/Object;")) {
                return CGLIB$clone$4$Proxy;
            }
            break;
        case 1826985398:
            if (var10000.equals("equals(Ljava/lang/Object;)Z")) {
                return CGLIB$equals$1$Proxy;
            }
            break;
        case 1913648695:
            if (var10000.equals("toString()Ljava/lang/String;")) {
                return CGLIB$toString$2$Proxy;
            }
            break;
        case 1984935277:
            if (var10000.equals("hashCode()I")) {
                return CGLIB$hashCode$3$Proxy;
            }
        }

        return null;
    }

    private static final void CGLIB$BIND_CALLBACKS(Object var0) {
        A$$EnhancerBySpringCGLIB$$21771092 var1 = (A$$EnhancerBySpringCGLIB$$21771092)var0;
        if (!var1.CGLIB$BOUND) {
            var1.CGLIB$BOUND = true;
            Object var10000 = CGLIB$THREAD_CALLBACKS.get();
            if (var10000 == null) {
                var10000 = CGLIB$STATIC_CALLBACKS;
                if (var10000 == null) {
                    return;
                }
            }

            Callback[] var10001 = (Callback[])var10000;
            var1.CGLIB$CALLBACK_6 = (MethodInterceptor)((Callback[])var10000)[6];
            var1.CGLIB$CALLBACK_5 = (MethodInterceptor)var10001[5];
            var1.CGLIB$CALLBACK_4 = (Dispatcher)var10001[4];
            var1.CGLIB$CALLBACK_3 = (Dispatcher)var10001[3];
            var1.CGLIB$CALLBACK_2 = (NoOp)var10001[2];
            var1.CGLIB$CALLBACK_1 = (MethodInterceptor)var10001[1];
            var1.CGLIB$CALLBACK_0 = (MethodInterceptor)var10001[0];
        }

    }

    public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] var0) {
        CGLIB$THREAD_CALLBACKS.set(var0);
    }

    public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] var0) {
        CGLIB$STATIC_CALLBACKS = var0;
    }

    public void setCallback(int var1, Callback var2) {
        switch(var1) {
        case 0:
            this.CGLIB$CALLBACK_0 = (MethodInterceptor)var2;
            break;
        case 1:
            this.CGLIB$CALLBACK_1 = (MethodInterceptor)var2;
            break;
        case 2:
            this.CGLIB$CALLBACK_2 = (NoOp)var2;
            break;
        case 3:
            this.CGLIB$CALLBACK_3 = (Dispatcher)var2;
            break;
        case 4:
            this.CGLIB$CALLBACK_4 = (Dispatcher)var2;
            break;
        case 5:
            this.CGLIB$CALLBACK_5 = (MethodInterceptor)var2;
            break;
        case 6:
            this.CGLIB$CALLBACK_6 = (MethodInterceptor)var2;
        }

    }

    public void setCallbacks(Callback[] var1) {
        this.CGLIB$CALLBACK_0 = (MethodInterceptor)var1[0];
        this.CGLIB$CALLBACK_1 = (MethodInterceptor)var1[1];
        this.CGLIB$CALLBACK_2 = (NoOp)var1[2];
        this.CGLIB$CALLBACK_3 = (Dispatcher)var1[3];
        this.CGLIB$CALLBACK_4 = (Dispatcher)var1[4];
        this.CGLIB$CALLBACK_5 = (MethodInterceptor)var1[5];
        this.CGLIB$CALLBACK_6 = (MethodInterceptor)var1[6];
    }

    public Callback getCallback(int var1) {
        CGLIB$BIND_CALLBACKS(this);
        Object var10000;
        switch(var1) {
        case 0:
            var10000 = this.CGLIB$CALLBACK_0;
            break;
        case 1:
            var10000 = this.CGLIB$CALLBACK_1;
            break;
        case 2:
            var10000 = this.CGLIB$CALLBACK_2;
            break;
        case 3:
            var10000 = this.CGLIB$CALLBACK_3;
            break;
        case 4:
            var10000 = this.CGLIB$CALLBACK_4;
            break;
        case 5:
            var10000 = this.CGLIB$CALLBACK_5;
            break;
        case 6:
            var10000 = this.CGLIB$CALLBACK_6;
            break;
        default:
            var10000 = null;
        }

        return (Callback)var10000;
    }

    public Callback[] getCallbacks() {
        CGLIB$BIND_CALLBACKS(this);
        return new Callback[]{this.CGLIB$CALLBACK_0, this.CGLIB$CALLBACK_1, this.CGLIB$CALLBACK_2, this.CGLIB$CALLBACK_3, this.CGLIB$CALLBACK_4, this.CGLIB$CALLBACK_5, this.CGLIB$CALLBACK_6};
    }

    public final boolean isExposeProxy() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).isExposeProxy();
    }

    public final TargetSource getTargetSource() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).getTargetSource();
    }

    public final Class getTargetClass() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((TargetClassAware)var10000.loadObject()).getTargetClass();
    }

    public final Advisor[] getAdvisors() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).getAdvisors();
    }

    public final void setExposeProxy(boolean var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).setExposeProxy(var1);
    }

    public final void setPreFiltered(boolean var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).setPreFiltered(var1);
    }

    public final void setTargetSource(TargetSource var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).setTargetSource(var1);
    }

    public final boolean isPreFiltered() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).isPreFiltered();
    }

    public final void addAdvisor(Advisor var1) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).addAdvisor(var1);
    }

    public final void addAdvisor(int var1, Advisor var2) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).addAdvisor(var1, var2);
    }

    public final boolean replaceAdvisor(Advisor var1, Advisor var2) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).replaceAdvisor(var1, var2);
    }

    public final boolean removeAdvisor(Advisor var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).removeAdvisor(var1);
    }

    public final void removeAdvisor(int var1) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).removeAdvisor(var1);
    }

    public final void addAdvice(Advice var1) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).addAdvice(var1);
    }

    public final void addAdvice(int var1, Advice var2) throws AopConfigException {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        ((Advised)var10000.loadObject()).addAdvice(var1, var2);
    }

    public final boolean removeAdvice(Advice var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).removeAdvice(var1);
    }

    public final Class[] getProxiedInterfaces() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).getProxiedInterfaces();
    }

    public final boolean isInterfaceProxied(Class var1) {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).isInterfaceProxied(var1);
    }

    public final boolean isProxyTargetClass() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).isProxyTargetClass();
    }

    public final String toProxyConfigString() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_4;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_4;
        }

        return ((Advised)var10000.loadObject()).toProxyConfigString();
    }

    static void CGLIB$STATICHOOK29() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class var0 = Class.forName("cn.xyz.god.wealth.A$$EnhancerBySpringCGLIB$$21771092");
        Class var1;
        Method[] var10000 = ReflectUtils.findMethods(new String[]{"equals", "(Ljava/lang/Object;)Z", "toString", "()Ljava/lang/String;", "hashCode", "()I", "clone", "()Ljava/lang/Object;"}, (var1 = Class.forName("java.lang.Object")).getDeclaredMethods());
        CGLIB$equals$1$Method = var10000[0];
        CGLIB$equals$1$Proxy = MethodProxy.create(var1, var0, "(Ljava/lang/Object;)Z", "equals", "CGLIB$equals$1");
        CGLIB$toString$2$Method = var10000[1];
        CGLIB$toString$2$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/String;", "toString", "CGLIB$toString$2");
        CGLIB$hashCode$3$Method = var10000[2];
        CGLIB$hashCode$3$Proxy = MethodProxy.create(var1, var0, "()I", "hashCode", "CGLIB$hashCode$3");
        CGLIB$clone$4$Method = var10000[3];
        CGLIB$clone$4$Proxy = MethodProxy.create(var1, var0, "()Ljava/lang/Object;", "clone", "CGLIB$clone$4");
        CGLIB$test$0$Method = ReflectUtils.findMethods(new String[]{"test", "()V"}, (var1 = Class.forName("cn.xyz.god.wealth.A")).getDeclaredMethods())[0];
        CGLIB$test$0$Proxy = MethodProxy.create(var1, var0, "()V", "test", "CGLIB$test$0");
    }

    final void CGLIB$test$0() {
        super.test();
    }

    final boolean CGLIB$equals$1(Object var1) {
        return super.equals(var1);
    }

    final String CGLIB$toString$2() {
        return super.toString();
    }

    final int CGLIB$hashCode$3() {
        return super.hashCode();
    }

    final Object CGLIB$clone$4() throws CloneNotSupportedException {
        return super.clone();
    }
}