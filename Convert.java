package com.example.mybatis.demo.util;

import java.lang.reflect.Field;

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

        Field[] fields = tClazz.getDeclaredFields();
        Class<?> superClazz = tClazz.getSuperclass();
        if (superClazz != Object.class){
            convertField(superClazz, s, t, superClazz.getDeclaredFields());
        }

        convertField(sClazz, s, t, fields);
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


    private static <S, T> void convertField(Class<?> sClazz, S s, T t, Field[] fields) throws IllegalAccessException {
        Field f;
        for (Field field : fields){
            try {
                f = sClazz.getDeclaredField(field.getName());
            }catch (NoSuchFieldException e){
//                try {
//                    f = sClazz.getSuperclass().getDeclaredField(field.getName());
//                } catch (NoSuchFieldException ex) {
//                    continue;
//                }
                continue;
            }
            f.setAccessible(true);
            field.setAccessible(true);
            field.set(t, f.get(s));
        }
    }
}
