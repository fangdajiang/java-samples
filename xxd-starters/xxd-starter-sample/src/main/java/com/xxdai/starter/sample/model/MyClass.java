package com.xxdai.starter.sample.model;

import com.xxdai.starter.sample.annotation.MyAnnotation;

/**
 *
 * @author fangdajiang
 * @date 2018/6/21
 */
@MyAnnotation(
        test = "类注解",
        value = "class annotation",
        name = "my class name",
        age = 99,
        isStudent = false,
        alias = {"name1", "name2"},
        favoriteColor = MyAnnotation.Color.RED
)
public class MyClass {
    @MyAnnotation(
            test = "方法注解",
            value = "method annotation",
            alias = {"name1", "name2", "name"}
    )
    public void myMethod(){

    }
}
