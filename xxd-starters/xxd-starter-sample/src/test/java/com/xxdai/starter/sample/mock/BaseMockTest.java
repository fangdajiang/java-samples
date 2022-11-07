package com.xxdai.starter.sample.mock;

import com.xxdai.starter.sample.service.DemoForestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * 状态测试，行为测试
 *
 * refer:
 * https://segmentfault.com/a/1190000006746409
 * https://blog.csdn.net/zhangxin09/article/details/42422643
 * https://blog.csdn.net/watertekhqx/article/details/71644036
 *
 * Created by fangdajiang on 2018/9/4.
 */
@Slf4j
public class BaseMockTest {

    @Test
    public void createMockObject() {
        //使用 mock 静态方法创建 Mock 对象.
        List mockedList = mock(List.class);
        assertTrue(mockedList != null);

        //mock 方法不仅可以 Mock 接口类, 还可以 Mock 具体的类型.
        ArrayList mockedArrayList = mock(ArrayList.class);
        assertTrue(mockedArrayList != null);
    }

    @Test
    public void configMockObject() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);

        //进行定制。when(​...).thenReturn(​...) 方法链不仅仅要匹配方法的调用, 而且要方法的参数一样才行

        //当调用 mockedList.add("one") 时, 返回 true
        when(mockedList.add("one")).thenReturn(true);
        assertTrue(mockedList.add("one"));
        //因为我们没有定制 add("two"), 因此返回默认值, 即 false.
        assertFalse(mockedList.add("two"));

        //直接调用 mockedList.size() 时，返回 0；直接调用 get(0) 时，返回 null
        log.debug("mockedList.size():{}, mockedList.get(0):{}", mockedList.size(), mockedList.get(0));

        //当配置 size() 后再调用 mockedList.size(), 返回 1
        when(mockedList.size()).thenReturn(1);
        assertEquals(1, mockedList.size());
        //当配置 get(0) 后再调用 get(0), 返回 another one
        when(mockedList.get(0)).thenReturn("another one");
        assertEquals("another one", mockedList.get(0));

        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello,").thenReturn("Mockito!");
        String result = i.next() + " " + i.next();
        assertEquals("Hello, Mockito!", result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testForIOException() throws Exception {
        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello,").thenReturn("Mockito!"); // 1
        String result = i.next() + " " + i.next(); // 2
        assertEquals("Hello, Mockito!", result);

        //doThrow(ExceptionX).when(x).methodCall: 当调用了 x.methodCall 方法后, 抛出异常 ExceptionX
        doThrow(new NoSuchElementException()).when(i).next(); // 3
        i.next(); // 4
    }

    /**
     * baeldung.com/mockito-verify
     * 校验一个方法曾经被调用过（N 次），或未曾被调用过，或其他方法未曾被调用过
     * 校验几个方法的调用顺序的正确性
     * 校验一个方法的调用使用了确切的/任意参数
     *
     * 假如不关心返回结果，而是侧重方法有否被正确的参数调用过
     */
    @Test
    public void testVerify() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);
        mockedList.add("one");
        mockedList.add("two");
        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        when(mockedList.size()).thenReturn(5);
        assertEquals(5, mockedList.size());

        //Mockito 会追踪 Mock 对象的所用方法调用和调用方法时所传递的参数
        verify(mockedList, atLeastOnce()).add("one");
        verify(mockedList, times(1)).add("two");
        //如果times不传入，则默认是1
        verify(mockedList).add("two");
        verify(mockedList, times(3)).add("three times");
        //校验 mockedList.isEmpty() 从未被调用
        verify(mockedList, never()).isEmpty();

        Map mockedMap = mock(Map.class);
        when(mockedMap.get("city")).thenReturn("上海");
        mockedMap.get("city");
        mockedMap.get("region");
        //关注参数有否传入
        verify(mockedMap).get( ArgumentMatchers.eq( "city" ) );
        //关注调用的次数
        verify(mockedMap, times( 2 )).get(anyString());
    }

    /**
     * 还可通过调用 when(xxx.xx()).thenCallRealMethod() 来调用真实的 API
     */
    @Test
    public void testSpy() {
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);

        //对 spy.size() 进行定制（部分模拟）.
        when(spy.size()).thenReturn(100);

        spy.add("one");
        spy.add("two");

        //因为我们没有对 get(0), get(1) 方法进行定制,
        //因此这些调用其实是调用的真实对象的方法.
        assertEquals("one", spy.get(0));
        assertEquals("two", spy.get(1));

        assertEquals(100, spy.size());
    }

    @Test
    public void testDoNothing() {
        List<String> list = new ArrayList<>();
        List<String> spy = spy(list);

        //make clear() do nothing
        doNothing().when(spy).clear();

        spy.add("one");

        //clear() does nothing, so the list still contains "one"
        spy.clear();

        assertEquals("one", spy.get(0));
    }

    @Test
    public void testDoNothingAndThrow() {
        DemoForestService demoForestService = mock(DemoForestService.class);
        doNothing().doThrow(new RuntimeException()).when(demoForestService).foo();

        //does nothing the first time
        demoForestService.foo();

        //throws RuntimeException the next time
        try {
            demoForestService.foo();
        } catch (RuntimeException e) {
            log.info("caught an exception");
        }
    }

    @Test
    public void testWhenCalledVerified() {
        @SuppressWarnings("unchecked")
        List<String> mockedArrayList = mock(ArrayList.class);
        doNothing().when(mockedArrayList).add(isA(Integer.class), isA(String.class)); //could be ignored
        mockedArrayList.add(0, "");

        verify(mockedArrayList, times(1)).add(0, "");
    }

    @Test
    public void testAnyArg() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);
        when(mockedList.get(anyInt())).thenReturn("boo");

        assertEquals(mockedList.get(99), "boo");
    }

    /**
     * 验证所传入方法的参数
     */
    @Test
    public void testCaptureArgument() {
        List<String> list = Arrays.asList("1", "2");
        @SuppressWarnings("unchecked")
        Collection<String> mockedList = mock(List.class);
        mockedList.addAll(list);

        //获取需要捕获的方法参数
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<String>> argument = ArgumentCaptor.forClass(List.class);
        //通过 argument.capture() 来获取 mockedList.addAll 方法所传递的实参 list
        verify(mockedList).addAll(argument.capture());
        //argument 中存入了之前对方法调用的值
        assertEquals(2, argument.getValue().size());
        assertEquals(list, argument.getValue());
    }

    @Test
    public void testAnswerWithCallback() {
        List mockedList = mock(List.class);
        //使用 answer 来生成我们期望的返回
        when(mockedList.get(anyInt())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return "hello world:" + args[0];
        });
        assertEquals("hello world:0", mockedList.get(0));
        assertEquals("hello world:999", mockedList.get(999));
    }

    @Test
    public void testUnstubbedInvocations() {
        //使用 answer 对未预设的调用返回默认期望值
        List mockedList = mock(List.class, invocation -> 999);
        //以下的 get(1) 因为没有预设，通常返回 null，但因为使用了 answer 改变了默认期望值
        assertEquals(999, mockedList.get(1));
        //以下的 size() 因为没有预设，通常返回 0，但因为使用了 answer 改变了默认期望值
        assertEquals(999, mockedList.size());
    }

}
