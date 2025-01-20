package com.arplanets.template.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;


@Slf4j
public class ClassUtil {

    public static HashMap<String , Field> getAllField(Object model ) {

        Class<?> clazz = model.getClass();
        HashMap<String , Field> list = new HashMap<>();

        while( clazz != null ) {
            Field[] files = clazz.getDeclaredFields();

            for (Field file : files) {
                list.put(file.getName(), file);
            }

            clazz = clazz.getSuperclass();
        }
        return list ;
    }
}
