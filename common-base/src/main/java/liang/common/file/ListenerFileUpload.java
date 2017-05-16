package liang.common.file;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liangzhiyan on 2017/5/11.
 */
public abstract class ListenerFileUpload {
    public ListenerFileUpload(File file) {
        execute(file, ",|\t|    | ", 1, true);
    }

    public ListenerFileUpload(File file, String lineSplit) {
        execute(file, lineSplit, 1, true);
    }

    public ListenerFileUpload(File file, String lineSplit, int consumerLines) {
        execute(file, lineSplit, consumerLines, true);
    }

    public ListenerFileUpload(File file, String lineSplit, int consumerLines, boolean exceptionContinue) {
        execute(file, lineSplit, consumerLines, exceptionContinue);
    }

    public void execute(File uploadFile, String lineSplit, int consumerLines, boolean exceptionContinue) {
        FileFactory.getFileHandler(uploadFile).readFile(uploadFile, lineSplit, consumerLines, exceptionContinue,
                new FileHandler.ConsumerData() {
                    @Override
                    public void readTitle(Set<String> title) {
                        title(title);
                    }

                    @Override
                    public void readBody(int offset, List<Map<String, Object>> lines) {
                        deal(offset, lines);
                    }
                });
    }

    public void title(Set<String> title) {
    }

    public abstract void deal(int offset, List<Map<String, Object>> lines);
}
