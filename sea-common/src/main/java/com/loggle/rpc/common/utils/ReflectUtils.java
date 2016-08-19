package com.loggle.rpc.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guomy
 * @create 2016-08-05 18:26.
 */
public class ReflectUtils {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";

    public static final String CLASS_DESC = "(?:L" + JAVA_IDENT_REGEX + "(?:\\/" + JAVA_IDENT_REGEX + ")*;)";

    public static final String ARRAY_DESC = "(?:\\[+(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "))";

    public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|" + CLASS_DESC + "|" + ARRAY_DESC + ")";

    public static final Pattern DESC_PATTERN = Pattern.compile(DESC_REGEX);

    private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    /**
     * get class array desc.
     * [int.class, boolean[].class, Object.class] => "I[ZLjava/lang/Object;"
     *
     * @param cs class array.
     * @return desc.
     * @throws
     */
    public static String getDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getDesc(c));
        return sb.toString();
    }

    /**
     * get class desc.
     * boolean[].class => "[Z"
     * Object.class => "Ljava/lang/Object;"
     *
     * @param c class.
     * @return desc.
     * @throws
     */
    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) ret.append(JVM_VOID);
            else if ("boolean".equals(t)) ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t)) ret.append(JVM_BYTE);
            else if ("char".equals(t)) ret.append(JVM_CHAR);
            else if ("double".equals(t)) ret.append(JVM_DOUBLE);
            else if ("float".equals(t)) ret.append(JVM_FLOAT);
            else if ("int".equals(t)) ret.append(JVM_INT);
            else if ("long".equals(t)) ret.append(JVM_LONG);
            else if ("short".equals(t)) ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    /**
     * get class array instance.
     *
     * @param desc desc.
     * @return Class class array.
     * @throws ClassNotFoundException
     */
    public static Class<?>[] desc2classArray(String desc) throws ClassNotFoundException {
        Class<?>[] ret = desc2classArray(Thread.currentThread().getContextClassLoader(), desc);
        return ret;
    }

    /**
     * get class array instance.
     *
     * @param cl   ClassLoader instance.
     * @param desc desc.
     * @return Class[] class array.
     * @throws ClassNotFoundException
     */
    private static Class<?>[] desc2classArray(ClassLoader cl, String desc) throws ClassNotFoundException {
        if (desc.length() == 0)
            return EMPTY_CLASS_ARRAY;

        List<Class<?>> cs = new ArrayList<Class<?>>();
        Matcher m = DESC_PATTERN.matcher(desc);
        while (m.find())
            cs.add(desc2class(cl, m.group()));
        return cs.toArray(EMPTY_CLASS_ARRAY);
    }

    /**
     * desc to class.
     * "[Z" => boolean[].class
     * "[[Ljava/util/Map;" => java.util.Map[][].class
     *
     * @param cl   ClassLoader instance.
     * @param desc desc.
     * @return Class instance.
     * @throws ClassNotFoundException
     */
    private static Class<?> desc2class(ClassLoader cl, String desc) throws ClassNotFoundException {
        switch (desc.charAt(0)) {
            case JVM_VOID:
                return void.class;
            case JVM_BOOLEAN:
                return boolean.class;
            case JVM_BYTE:
                return byte.class;
            case JVM_CHAR:
                return char.class;
            case JVM_DOUBLE:
                return double.class;
            case JVM_FLOAT:
                return float.class;
            case JVM_INT:
                return int.class;
            case JVM_LONG:
                return long.class;
            case JVM_SHORT:
                return short.class;
            case 'L':
                desc = desc.substring(1, desc.length() - 1).replace('/', '.'); // "Ljava/lang/Object;" ==> "java.lang.Object"
                break;
            case '[':
                desc = desc.replace('/', '.');  // "[[Ljava/lang/Object;" ==> "[[Ljava.lang.Object;"
                break;
            default:
                throw new ClassNotFoundException("Class not found: " + desc);
        }

        if (cl == null)
            cl = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = DESC_CLASS_CACHE.get(desc);
        if (clazz == null) {
            clazz = Class.forName(desc, true, cl);
            DESC_CLASS_CACHE.put(desc, clazz);
        }
        return clazz;
    }
}
