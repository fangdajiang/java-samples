package com.xxdai.starter.experiment.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Iterator;

/**
 * 使用 Jsoup
 * Created by fangdajiang on 2018/9/18.
 */
@Slf4j
public class JavaScriptParser {

    /**
     * 解析出 script 代码中的 src 属性值
     * @param jsText 形如： <script type="text/javascript" src="http..." reload="1"></script>
     * @return src 的值
     */
    public static final String extractSourceAttrValue(String jsText) throws ParseException {
        String src = null;
        Document doc = Jsoup.parse(jsText);
        Elements elements = doc.getElementsByTag("script");
        if (null != elements && 1 == elements.size()) {
            Element element = elements.get(0);
            log.debug("doc:{}, element:{}", doc, element);
            Iterator iterator = element.getElementsByAttribute("src").iterator();
            while (iterator.hasNext()) {
                Element ele = (Element)iterator.next();
                log.debug("ele:{}", ele);
                src = ele.attr("src");
                break;
            }
        } else {
            throw new ParseException(jsText, 0);
        }
        return src;
    }
}
