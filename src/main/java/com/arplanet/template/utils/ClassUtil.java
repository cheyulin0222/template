package com.arplanet.template.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

@Slf4j
public class ClassUtil {

    public static HashMap<String , Field> getAllField(Object model ) {

        Class<?> clazz = model.getClass();
        HashMap<String , Field> list = new HashMap<String , Field>();
        String className = clazz.getName() ;

        while( clazz != null ) {
            Field[] files = clazz.getDeclaredFields();

            for ( int i = 0 ; i < files.length ; i++ ) {
                Field file = files[i];
                list.put(file.getName(), file);
            }

            clazz = clazz.getSuperclass();
        }
        return list ;
    }

    public static HashMap<String , Method> getMethod(Object model ) {

        Class<?> clazz = model.getClass();
        HashMap<String , Method> list = new HashMap<String , Method>();
        String className = clazz.getName() ;

        while( clazz != null ) {
            Method[] methods = clazz.getDeclaredMethods();

            for ( int i = 0 ; i < methods.length ; i++ ) {
                Method file = methods[i];
                list.put(file.getName(), file);
            }

            clazz = clazz.getSuperclass();
        }
        return list ;
    }

    public static Method getSpecifyMethod(Object object, String methodName) {
        Class<?> clazz = object.getClass();
        try {

            Method method = clazz.getMethod(methodName);

            return method;
        } catch (NoSuchMethodException e) {
            // 如果方法不存在，返回 null
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getInvokeString(Method method, Object object) {
        try {
            return (String) method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用於取得物件的指定方法
     * @param object 物件
     * @param methodName 欲使用的方法名稱
     * @return
     */
    public static String getMethodToString(Object object, String methodName) {
        Class<?> clazz = object.getClass();
        try {
            Method method = clazz.getMethod(methodName);
            return (String) method.invoke(object);
        } catch (NoSuchMethodException e) {
            // 如果方法不存在，返回 ""
            log.error(object.getClass().getSimpleName()+"中 "+methodName+" 方法不存在");
        } catch (Exception e) {
            log.error("getMethodToString Exception:" + e , e);
        }
        return "";
    }
}
