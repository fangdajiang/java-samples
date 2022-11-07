package com.xxdai.starter.sample.mock;

import com.xxdai.starter.sample.dao.DemoForestDao;
import com.xxdai.starter.sample.service.impl.DemoForestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用注解测试 sample 里的 service 和 dao
 * Created by fangdajiang on 2018/9/6.
 */
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class AnnotationMockTest {
    @Mock
    private DemoForestDao demoForestDao;
    @InjectMocks
    private DemoForestServiceImpl demoForestService;

    @Test
    public void acquireDwz() {
        Map<String, Object> tinyUrlMap = new HashMap<>();
        tinyUrlMap.put("tinyurl", "http://ttt");
        when(demoForestDao.acquireDuanWangZhiUrl(anyString())).thenReturn(tinyUrlMap);
        String dwzUrl = demoForestService.acquireDwz("http://www.xinxindai.com");
        log.debug("dwzUrl:{}", dwzUrl);
        assertFalse(dwzUrl.isEmpty());
    }

    @Captor
    private ArgumentCaptor<List<Integer>> captor;
    @Mock
    private Foo mockedFoo;

    @Test
    public void captureArgument() {
        mockedFoo.doStuff(Arrays.asList(1, 2, 3));

        verify(mockedFoo).doStuff(captor.capture());
        assertEquals(Arrays.asList(1, 2, 3), captor.getValue());
    }
    static class Foo {
        void doStuff(List<Integer> integerList) {
            log.debug("doing staff:{}", integerList);
        }
    }
}
