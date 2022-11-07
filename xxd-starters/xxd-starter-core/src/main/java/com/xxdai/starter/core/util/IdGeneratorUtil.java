package com.xxdai.starter.core.util;

import com.xxdai.pub.common.model.IdentityObj;
import com.xxdai.pub.common.service.BizService;
import com.xxdai.pub.common.util.DateUtil;
import com.xxdai.pub.common.util.SpringUtil;
import com.xxdai.pub.constant.CodeEnumCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fangdajiang on 2017/4/27.
 */
@Slf4j
public class IdGeneratorUtil {

    //Suppress default constructor for noninstantiability
    private IdGeneratorUtil() {}

    private static final byte ID_PREFIX_MAX_LENGTH = 10;
    private static final byte SEQUENCE_LENGTH = 7;
    private static final char SEQUENCE_PAD_CHAR = '0';

    public static IdentityObj generateOrderId(String prefixStr, String sequenceName) {
        return generateOrderId(prefixStr,sequenceName,SEQUENCE_LENGTH,SEQUENCE_PAD_CHAR, DateUtil.ShortCompactDateFormat);
    }

    public static IdentityObj generateOrderId(String prefixStr, String sequenceName,String datePattern) {
        return generateOrderId(prefixStr,sequenceName,SEQUENCE_LENGTH,SEQUENCE_PAD_CHAR,datePattern);
    }

    public static IdentityObj generateOrderId(String prefixStr,String sequenceName,int sequenceLength) {
        return generateOrderId(prefixStr,sequenceName,sequenceLength,SEQUENCE_PAD_CHAR,DateUtil.ShortCompactDateFormat);
    }

    public static IdentityObj generateOrderId(String prefixStr,String sequenceName,int sequenceLength,String datePattern) {
        return generateOrderId(prefixStr,sequenceName,sequenceLength,SEQUENCE_PAD_CHAR,datePattern);
    }

    public static IdentityObj generateOrderId(String prefixStr,String sequenceName,int sequenceLength,char padChar) {
        return generateOrderId(prefixStr,sequenceName,sequenceLength,padChar,DateUtil.ShortCompactDateFormat);
    }
    public static IdentityObj generateOrderId(String prefixStr,String sequenceName,int sequenceLength,char padChar,String datePattern) {
        if (prefixStr.length() > ID_PREFIX_MAX_LENGTH) {
            throw new IllegalArgumentException("length of BizIdPrefix's name should <= " + sequenceLength);
        } else {
            BizService bizService = SpringUtil.getBean(BizService.class);
            long sequenceNum = bizService.querySequence(sequenceName);
            String sequenceStr = sequenceNum+"";
            sequenceStr = sequenceStr.length() > sequenceLength ? sequenceStr.substring(sequenceStr.length()-sequenceLength) : sequenceStr;
            String idStr = assembleId(prefixStr, sequenceStr,sequenceLength,padChar,datePattern);
            return new IdentityObj(sequenceNum,idStr);
        }
    }

    /**
     * 业务订单号规则： 枚举业务前缀+年月日+7位流水 <= 30, 业务前缀长度 <= 10
     * @param bizIdPrefix 即 CodeEnumCollection.BizIdPrefix 中的枚举类型，比如 OPEN_ACNT
     * @return
     */
    public static String generateOrderId(CodeEnumCollection.BizIdPrefix bizIdPrefix) {
        return generateOrderId(bizIdPrefix.name(),getSequenceName(bizIdPrefix).getMessage(),SEQUENCE_LENGTH,SEQUENCE_PAD_CHAR).getIdStr();
    }

    public static CodeEnumCollection.SequenceName getSequenceName(CodeEnumCollection.BizIdPrefix bizIdPrefix) {
        return CodeEnumCollection.SequenceName.valueOf(bizIdPrefix.name());
    }

    private static String assembleId(String busiCodeRule, String sequenceNum,int sequenceLength,char padChar,String datePattern) {
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(busiCodeRule);
        returnValue.append(DateUtil.format(new Date(),datePattern));
        returnValue.append(StringUtils.leftPad(sequenceNum, sequenceLength, padChar));
        return returnValue.toString();
    }
}
