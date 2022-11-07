package com.xxdai.starter.cache.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * created by xiguoding on 2018/3/23 下午4:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RequestStatusTest {

    @Test
    public void requestStatus() {
        boolean initiated = RequestStatus.valueOf("INITIATED").isFinished();
        assertFalse(initiated);

        boolean finished = RequestStatus.valueOf("FINISHED").isFinished();
        assertTrue(finished);
    }
}
