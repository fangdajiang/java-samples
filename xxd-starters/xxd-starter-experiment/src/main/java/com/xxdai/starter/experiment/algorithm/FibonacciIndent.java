package com.xxdai.starter.experiment.algorithm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * 裴波那契"缩进"算法的实现
 *
 * Created by fangdajiang on 2018/10/16.
 */
public class FibonacciIndent {
    private static final Logger log = (Logger)LoggerFactory.getLogger(FibonacciIndent.class);
    private static final char[] LOWERCASE_ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 根据输入字母配对打印裴波那契"缩进"
     *
     * 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
     *
     * a {
     *  b {
     *  c {
     *   d {
     *    e {
     *      f {
     *         g {
     *              h {
     *                      i {
     *                                   j {
     *                                   ...
     *                                   }
     *                      }
     *              }
     *         }
     *      }
     *    }
     *   }
     *  }
     *  }
     * }
     *
     */
    public static void main(String[] args) {
        log.setLevel(Level.INFO);
        char inputSingleLowercaseLetter = 'j';

        String lowercaseAlphabet = new String(LOWERCASE_ALPHABET);
        log.debug("lowercaseAlphabet:{}, inputSingleLowercaseLetter:{}", lowercaseAlphabet, inputSingleLowercaseLetter);

        if (lowercaseAlphabet.contains(String.valueOf(inputSingleLowercaseLetter))) {
            int indexLetter = lowercaseAlphabet.indexOf(inputSingleLowercaseLetter);
            printFibonacciIndent(indexLetter, false);
        } else {
            log.warn("input legal lowercase alphabet letter instead of:{}", inputSingleLowercaseLetter);
        }
    }

    private static void printFibonacciIndent(int indexLetter, boolean reachEnd) {
        log.debug("indexLetter:{}, reachEnd:{}", indexLetter, reachEnd);
        int spaceCount, startIndex;
        if (reachEnd) {
            startIndex = indexLetter;
        } else {
            startIndex = 0;
        }
        for (int line = startIndex; ;) {
            log.debug("line:{}", line);
            if (reachEnd ? line >= 0 : line <= indexLetter) {
                spaceCount = calSpaceCount(line);
                log.debug("current space count:{}", spaceCount);
                printSpaces(spaceCount);
                if (reachEnd) {
                    log.debug("ready to print END when reaching an end, line:{}", line);
                    System.out.println("}");
                    line--;
                } else {
                    log.debug("ready to print START when not an end, line:{}", line);
                    System.out.println(LOWERCASE_ALPHABET[line] + " {");
                    if (line < indexLetter) {
                        log.debug("ready to break when line:{} is less than indexLetter:{}", line, indexLetter);
                        line++;
                    } else {
                        log.debug("ready to print when line:{} is NOT less than indexLetter:{}", line, indexLetter);
                        printFibonacciIndent(line, line == indexLetter);
                    }
                }
            } else {
                log.info("READY TO STOP");
                System.exit(0);
            }
        }
    }

    private static int calSpaceCount(int line) {
        int spaceCount;
        if (line == 0) {
            spaceCount = 0;
        } else if (line == 1) {
            spaceCount = 1;
        } else {
            spaceCount = calSpaceCount(line - 1) + calSpaceCount(line - 2);
        }
        return spaceCount;
    }

    private static void printSpaces(int spaceCount) {
        for (int j = 0; j < spaceCount; j++) {
            System.out.print(" ");
        }
    }
}
