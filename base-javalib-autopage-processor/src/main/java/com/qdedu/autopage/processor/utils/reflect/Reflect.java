//package com.qdedu.autopage.processor.utils.reflect;
//
//import java.lang.reflect.AccessibleObject;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Member;
//import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.Proxy;
//import java.util.Arrays;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//
//public class Reflect {
//
//
//    public static Reflect on(String name) throws ReflectException {
//        return on(forName(name));
//    }
//
//
//    public static Reflect on(String name, ClassLoader classLoader) throws ReflectException {
//        return on(forName(name, classLoader));
//    }
//
//
//    public static Reflect on(Class<?> clazz) {
//        return new Reflect(clazz);
//    }
//
//
//    public static Reflect on(Object object) {
//        return new Reflect(object);
//    }
//
//
//    public static <T extends AccessibleObject> T accessible(T accessible) {
//        if (accessible == null) {
//            return null;
//        }
//
//        if (accessible instanceof Member) {
//            Member member = (Member) accessible;
//
//            if (Modifier.isPublic(member.getModifiers()) &&
//                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
//
//                return accessible;
//            }
//        }
//
//        // [jOOQ #3392] The accessible flag is set to false by default, also for public members.
//        if (!accessible.isAccessible()) {
//            accessible.setAccessible(true);
//        }
//
//        return accessible;
//    }
//
//    // ---------------------------------------------------------------------
//    // Members
//    // ---------------------------------------------------------------------
//
//
//    private final Object  object;
//
//
//    private final boolean isClass;
//
//    // ---------------------------------------------------------------------
//    // Constructors
//    // ---------------------------------------------------------------------
//
//    private Reflect(Class<?> type) {
//        this.object = type;
//        this.isClass = true;
//    }
//
//    private Reflect(Object object) {
//        this.object = object;
//        this.isClass = false;
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public <T> T get() {
//        return (T) object;
//    }
//
//
//    public Reflect set(String name, Object value) throws ReflectException {
//        try {
//            Field field = field0(name);
//            field.set(object, unwrap(value));
//            return this;
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//
//    public <T> T get(String name) throws ReflectException {
//        return field(name).<T>get();
//    }
//
//
//    public Reflect field(String name) throws ReflectException {
//        try {
//            Field field = field0(name);
//            return on(field.get(object));
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//    private Field field0(String name) throws ReflectException {
//        Class<?> type = type();
//
//        // Try getting a public field
//        try {
//            return type.getField(name);
//        }
//
//        // Try again, getting a non-public field
//        catch (NoSuchFieldException e) {
//            do {
//                try {
//                    return accessible(type.getDeclaredField(name));
//                }
//                catch (NoSuchFieldException ignore) {}
//
//                type = type.getSuperclass();
//            }
//            while (type != null);
//
//            throw new ReflectException(e);
//        }
//    }
//
//
//    public Map<String, Reflect> fields() {
//        Map<String, Reflect> result = new LinkedHashMap<String, Reflect>();
//        Class<?> type = type();
//
//        do {
//            for (Field field : type.getDeclaredFields()) {
//                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
//                    String name = field.getName();
//
//                    if (!result.containsKey(name))
//                        result.put(name, field(name));
//                }
//            }
//
//            type = type.getSuperclass();
//        }
//        while (type != null);
//
//        return result;
//    }
//
//
//    public Reflect call(String name) throws ReflectException {
//        return call(name, new Object[0]);
//    }
//
//
//    public Reflect call(String name, Object... args) throws ReflectException {
//        Class<?>[] types = types(args);
//
//        // Try invoking the "canonical" method, i.e. the one with exact
//        // matching argument types
//        try {
//            Method method = exactMethod(name, types);
//            return on(method, object, args);
//        }
//
//        // If there is no exact match, try to find a method that has a "similar"
//        // signature if primitive argument types are converted to their wrappers
//        catch (NoSuchMethodException e) {
//            try {
//                Method method = similarMethod(name, types);
//                return on(method, object, args);
//            } catch (NoSuchMethodException e1) {
//                throw new ReflectException(e1);
//            }
//        }
//    }
//
//    private Method exactMethod(String name, Class<?>[] types) throws NoSuchMethodException {
//        Class<?> type = type();
//
//        // first priority: find a public method with exact signature match in class hierarchy
//        try {
//            return type.getMethod(name, types);
//        }
//
//        // second priority: find a private method with exact signature match on declaring class
//        catch (NoSuchMethodException e) {
//            do {
//                try {
//                    return type.getDeclaredMethod(name, types);
//                }
//                catch (NoSuchMethodException ignore) {}
//
//                type = type.getSuperclass();
//            }
//            while (type != null);
//
//            throw new NoSuchMethodException();
//        }
//    }
//
//
//    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
//        Class<?> type = type();
//
//        // first priority: find a public method with a "similar" signature in class hierarchy
//        // similar interpreted in when primitive argument types are converted to their wrappers
//        for (Method method : type.getMethods()) {
//            if (isSimilarSignature(method, name, types)) {
//                return method;
//            }
//        }
//
//        // second priority: find a non-public method with a "similar" signature on declaring class
//        do {
//            for (Method method : type.getDeclaredMethods()) {
//                if (isSimilarSignature(method, name, types)) {
//                    return method;
//                }
//            }
//
//            type = type.getSuperclass();
//        }
//        while (type != null);
//
//        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + ".");
//    }
//
//
//    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
//        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
//    }
//
//
//    public Reflect create() throws ReflectException {
//        return create(new Object[0]);
//    }
//
//
//    public Reflect create(Object... args) throws ReflectException {
//        Class<?>[] types = types(args);
//
//        // Try invoking the "canonical" constructor, i.e. the one with exact
//        // matching argument types
//        try {
//            Constructor<?> constructor = type().getDeclaredConstructor(types);
//            return on(constructor, args);
//        }
//
//        // If there is no exact match, try to find one that has a "similar"
//        // signature if primitive argument types are converted to their wrappers
//        catch (NoSuchMethodException e) {
//            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
//                if (match(constructor.getParameterTypes(), types)) {
//                    return on(constructor, args);
//                }
//            }
//
//            throw new ReflectException(e);
//        }
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public <P> P as(Class<P> proxyType) {
//        final boolean isMap = (object instanceof Map);
//        final InvocationHandler handler = new InvocationHandler() {
//            @SuppressWarnings("null")
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                String name = method.getName();
//
//                // Actual method name matches always come first
//                try {
//                    return on(object).call(name, args).get();
//                }
//
//                // [#14] Emulate POJO behaviour on wrapped map objects
//                catch (ReflectException e) {
//                    if (isMap) {
//                        Map<String, Object> map = (Map<String, Object>) object;
//                        int length = (args == null ? 0 : args.length);
//
//                        if (length == 0 && name.startsWith("get")) {
//                            return map.get(property(name.substring(3)));
//                        }
//                        else if (length == 0 && name.startsWith("is")) {
//                            return map.get(property(name.substring(2)));
//                        }
//                        else if (length == 1 && name.startsWith("set")) {
//                            map.put(property(name.substring(3)), args[0]);
//                            return null;
//                        }
//                    }
//
//                    throw e;
//                }
//            }
//        };
//
//        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
//    }
//
//
//    private static String property(String string) {
//        int length = string.length();
//
//        if (length == 0) {
//            return "";
//        }
//        else if (length == 1) {
//            return string.toLowerCase();
//        }
//        else {
//            return string.substring(0, 1).toLowerCase() + string.substring(1);
//        }
//    }
//
//
//    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
//        if (declaredTypes.length == actualTypes.length) {
//            for (int i = 0; i < actualTypes.length; i++) {
//                if (actualTypes[i] == NULL.class)
//                    continue;
//
//                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
//                    continue;
//
//                return false;
//            }
//
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//
//    @Override
//    public int hashCode() {
//        return object.hashCode();
//    }
//
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof Reflect) {
//            return object.equals(((Reflect) obj).get());
//        }
//
//        return false;
//    }
//
//
//    @Override
//    public String toString() {
//        return object.toString();
//    }
//
//    private static Reflect on(Constructor<?> constructor, Object... args) throws ReflectException {
//        try {
//            return on(accessible(constructor).newInstance(args));
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//
//    private static Reflect on(Method method, Object object, Object... args) throws ReflectException {
//        try {
//            accessible(method);
//
//            if (method.getReturnType() == void.class) {
//                method.invoke(object, args);
//                return on(object);
//            }
//            else {
//                return on(method.invoke(object, args));
//            }
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//
//    private static Object unwrap(Object object) {
//        if (object instanceof Reflect) {
//            return ((Reflect) object).get();
//        }
//
//        return object;
//    }
//
//
//    private static Class<?>[] types(Object... values) {
//        if (values == null) {
//            return new Class[0];
//        }
//
//        Class<?>[] result = new Class[values.length];
//
//        for (int i = 0; i < values.length; i++) {
//            Object value = values[i];
//            result[i] = value == null ? NULL.class : value.getClass();
//        }
//
//        return result;
//    }
//
//
//    private static Class<?> forName(String name) throws ReflectException {
//        try {
//            return Class.forName(name);
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//    private static Class<?> forName(String name, ClassLoader classLoader) throws ReflectException {
//        try {
//            return Class.forName(name, true, classLoader);
//        }
//        catch (Exception e) {
//            throw new ReflectException(e);
//        }
//    }
//
//
//    public Class<?> type() {
//        if (isClass) {
//            return (Class<?>) object;
//        }
//        else {
//            return object.getClass();
//        }
//    }
//
//
//    public static Class<?> wrapper(Class<?> type) {
//        if (type == null) {
//            return null;
//        }
//        else if (type.isPrimitive()) {
//            if (boolean.class == type) {
//                return Boolean.class;
//            }
//            else if (int.class == type) {
//                return Integer.class;
//            }
//            else if (long.class == type) {
//                return Long.class;
//            }
//            else if (short.class == type) {
//                return Short.class;
//            }
//            else if (byte.class == type) {
//                return Byte.class;
//            }
//            else if (double.class == type) {
//                return Double.class;
//            }
//            else if (float.class == type) {
//                return Float.class;
//            }
//            else if (char.class == type) {
//                return Character.class;
//            }
//            else if (void.class == type) {
//                return Void.class;
//            }
//        }
//
//        return type;
//    }
//
//    private static class NULL {}
//}
