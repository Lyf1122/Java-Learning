package org.example.utils;

import org.example.annotations.PrintInfo;

import java.lang.reflect.Field;

public class AnnotationHelper {
  public void printInfoAnnotation(Object handler) {
    Class<?> clazz = handler.getClass();
    if (clazz.isAnnotationPresent(PrintInfo.class)) {
      // Process class-level annotation
      PrintInfo annotation = clazz.getAnnotation(PrintInfo.class);
      System.out.println("Class annotation info: " + annotation.value());
    }

    // Optionally process field-level annotations
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(PrintInfo.class)) {
        PrintInfo fieldAnnotation = field.getAnnotation(PrintInfo.class);
        System.out.println("Field annotation info: " + fieldAnnotation.value());
      }
    }
  }
}
