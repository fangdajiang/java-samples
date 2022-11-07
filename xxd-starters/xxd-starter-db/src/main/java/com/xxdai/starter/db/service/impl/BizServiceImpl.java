package com.xxdai.starter.db.service.impl;

import com.xxdai.pub.common.service.BizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author fangdajiang
 * @date 2017/4/27
 */
public class BizServiceImpl implements BizService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Long querySequence(String sequenceName) {
        assert null != jdbcTemplate : "jdbcTemplate can't be null";
        StringBuilder sb = new StringBuilder("select ");
        sb.append(sequenceName).append(".nextVal from dual");
        return jdbcTemplate.queryForObject(sb.toString(), Long.class);
    }
}
