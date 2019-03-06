package com.liang.utils;

/**
 * Created by mc-050 on 2017/1/16 15:09.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class ExceptionHandler {
    public static void throwException(Throwable e) {
        if (e == null) {
            NullPointerException ex = new NullPointerException();
            int size = ex.getStackTrace().length - 1;
            StackTraceElement[] st = new StackTraceElement[size];
            System.arraycopy(ex.getStackTrace(), 1, st, 0, size);
            ex.setStackTrace(st);
            throw ex;
        }
        throwRuntimeException(e);
    }

    public static void tryNullException(Object obj, String info) {
        tryNullException(obj, new NullPointerException(info));
    }

    public static void tryNullException(Object obj, Exception cause) {
        if (obj == null) {
            throwException(cause);
        }
    }

    public static void tryThrowException(boolean expression, String info) {
        tryThrowException(expression, new RuntimeException(info));
    }

    public static void tryThrowException(boolean expression, Exception cause) {
        if (expression) {
            throwException(cause);
        }
    }

    private static <T extends Throwable> void throwRuntimeException(Throwable e) {
        throw new RuntimeException(e);
    }
}
