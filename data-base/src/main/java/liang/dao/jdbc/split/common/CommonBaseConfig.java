package liang.dao.jdbc.split.common;

/**
 * Created by liangzhiyan on 2017/7/10.
 */
public class CommonBaseConfig {

    private static boolean isPrintLog;

    private static boolean isSplitTable;

    private static boolean isThrowEx;

    private static boolean isCreateTable;

    private static boolean isSplitDb;

    public static boolean isIsPrintLog() {
        return isPrintLog;
    }

    public static void setIsPrintLog(boolean isPrintLog) {
        CommonBaseConfig.isPrintLog = isPrintLog;
    }

    public static boolean isIsSplitTable() {
        return isSplitTable;
    }

    public static void setIsSplitTable(boolean isSplitTable) {
        CommonBaseConfig.isSplitTable = isSplitTable;
    }

    public static boolean isIsThrowEx() {
        return isThrowEx;
    }

    public static void setIsThrowEx(boolean isThrowEx) {
        CommonBaseConfig.isThrowEx = isThrowEx;
    }

    public static boolean isIsCreateTable() {
        return isCreateTable;
    }

    public static void setIsCreateTable(boolean isCreateTable) {
        CommonBaseConfig.isCreateTable = isCreateTable;
    }

    public static boolean isIsSplitDb() {
        return isSplitDb;
    }

    public static void setIsSplitDb(boolean isSplitDb) {
        CommonBaseConfig.isSplitDb = isSplitDb;
    }
}
