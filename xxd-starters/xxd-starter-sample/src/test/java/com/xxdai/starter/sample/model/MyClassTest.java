package com.xxdai.starter.sample.model;

import com.xxdai.starter.sample.annotation.MyAnnotation;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 *
 * Created by fangdajiang on 2018/6/21.
 */
public class MyClassTest {
    @Test
    public void myClassMethodSubClass() throws Exception {
        //获取类MyClass的注解
        Class class0 = Class.forName("com.xxdai.starter.sample.model.MyClass");
        boolean exist = class0.isAnnotationPresent(MyAnnotation.class);
        System.out.println("MyAnnotation on MyClass exists: " + exist);

        Annotation[] annotations = class0.getAnnotations();
        for (Annotation annotation:annotations) {
            if(annotation instanceof MyAnnotation){
                printAnnotationFields("MyClass", annotation);
            }
        }
        //获取类MyClass中方法的注解
        Method[] methods = class0.getMethods();
        for(Method method:methods){
            if(method.isAnnotationPresent(MyAnnotation.class)){
                MyAnnotation myAnnotation = method.getAnnotation(MyAnnotation.class);
                printAnnotationFields("myMethod", myAnnotation);
            }
        }
        //获取类MyClass子类MySubClass的注解
        Class class1 = MySubClass.class;
        if(class1.isAnnotationPresent(MyAnnotation.class)){
            MyAnnotation myAnnotation = (MyAnnotation) class1.getAnnotation(MyAnnotation.class);
            printAnnotationFields("MySubClass", myAnnotation);
        }
    }

    private void printAnnotationFields(String annotatedObjectType, Annotation annotation) {
        System.out.println(annotatedObjectType + ":"+((MyAnnotation) annotation).test() +
                ", value:" + ((MyAnnotation) annotation).value() +
                ", name:" + ((MyAnnotation) annotation).name() +
                ", age:" + ((MyAnnotation) annotation).age() +
                ", isStudent:" + ((MyAnnotation) annotation).isStudent() +
                ", alias.length:" + ((MyAnnotation) annotation).alias().length +
                ", favoriteColor:" + ((MyAnnotation) annotation).favoriteColor()
        );
    }

}