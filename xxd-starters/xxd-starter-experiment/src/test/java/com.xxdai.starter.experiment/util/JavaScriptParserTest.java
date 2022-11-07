package com.xxdai.starter.experiment.util;

import com.xxdai.starter.experiment.util.JavaScriptParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by fangdajiang on 2018/9/18.
 */
@Slf4j
public class JavaScriptParserTest {

    private static final String SAMPLE = "<script type='text/javascript' src='http://bbs.xinxindai.com/api/uc.php?time=1536802674&code=582bM4P2BveC8BsEPOlSq5Izk731pJ1qZlFTi%2FZ3%2BxIG6owWpVcRRLtCP5ePG2%2F8YZQ4CZvU7sTNN4B%2B9Aac3TM53yrCOAXNSU6hl0FAGEr1M9YA%2FZFZYkM2apwh0VQR5TdWL0YOTL0R%2BTBC%2BCitZmUqqukq0nF5GapdbCw5EVgLn%2FszDw' reload='1'></script>";
    @Test
    public void extractSourceAttrValue() throws Exception {
        String src = JavaScriptParser.extractSourceAttrValue(SAMPLE);
        log.debug("src:{}", src);
    }

}