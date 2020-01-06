package common;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Leven
 * @date 2020/1/3 22:44
 */
public class Convert {

    /**
     * 用于model转vo，要求转换的变量的名称和类型相同，若vo存在model中没有的变量，则转换该变量后null。
     * 性能问题：相较于使用set方式，性能影响几乎可以忽略不计
     * @param t 转换目标对象，即vo
     * @param s 转换源对象，即model
     */
    public static <S, T> void convert(S s, T t) throws IllegalAccessException {
        Class<?> tClazz = t.getClass();
        Class<?> sClazz = s.getClass();

        convertField(sClazz, s, t, getFieldMap(tClazz));
    }

    /**
     *
     * @param exclusions vo中不进行转换的field名
     */
    public static <S, T> void convert(S s, T t, String... exclusions) throws IllegalAccessException {
        Class<?> tClazz = t.getClass();
        Class<?> sClazz = s.getClass();

        convertField(sClazz, s, t, getFieldMap(tClazz, exclusions));
    }

    /**
     *
     * @param s 转换源对象，即model
     * @param tClazz 转换目标对象的类
     * @return 返回目标对象
     */
    public static <S, T> T convert(S s, Class<T> tClazz) throws IllegalAccessException, InstantiationException {
        T t = tClazz.newInstance();
        convert(s, t);
        return t;
    }

    public static <S, T> T convert(S s, Class<T> tClazz, String exclusions) throws IllegalAccessException, InstantiationException {
        T t = tClazz.newInstance();
        convert(s, t, exclusions);
        return t;
    }


    /**
     *
     * @param sClazz 源对象的类
     * @param s 源对象
     * @param t 目标对象
     * @param tFieldMap 目标对象的所有Field，包括其所有父类的Field（Object除外）
     */
    private static <S, T> void convertField(Class<?> sClazz, S s, T t, Map<String, Field> tFieldMap) throws IllegalAccessException {
        Map<String, Field> sFieldMap = getFieldMap(sClazz);
        Field f;
        for (Map.Entry<String, Field> entry : tFieldMap.entrySet()){
            String fieldName = entry.getKey();
            if (sFieldMap.containsKey(fieldName)){
                f = sFieldMap.get(fieldName);
                f.setAccessible(true);
                Field field = entry.getValue();
                field.setAccessible(true);
                field.set(t, f.get(s));
            }
        }
    }

    /**
     * 获取类的所有field，如果子类中存在与父类相同名子的field，则丢弃父类中的field
     * @param clazz 类
     * @return Map<String, Field> Map<属性名, Field对象>
     */
    private static Map<String, Field> getFieldMap(Class<?> clazz){
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> superClazz = clazz;
        while (superClazz != Object.class){
            for (Field field: superClazz.getDeclaredFields()){
                if (!fieldMap.containsKey(field.getName())){
                    fieldMap.put(field.getName(), field);
                }
            }
            superClazz = superClazz.getSuperclass();
        }
        return fieldMap;
    }

    private static Map<String, Field> getFieldMap(Class<?> clazz, String... exclusions){
        Map<String, Field> fieldMap = getFieldMap(clazz);
        for (String exclusion : exclusions){
            fieldMap.remove(exclusion);
        }
        return fieldMap;
    }
}
