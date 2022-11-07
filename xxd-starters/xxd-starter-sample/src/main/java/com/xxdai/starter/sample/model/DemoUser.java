package com.xxdai.starter.sample.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fangdajiang
 * @date 2018/5/28
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class DemoUser implements java.io.Serializable {
    private static final long serialVersionUID = -2164058270260403154L;

    private Integer id;
    private String name;
    private Integer age;
}
