package bvs.logcollector;

import java.util.List;
import java.util.stream.Collectors;

public class LogRecordGroup {
    public static final String LOG_PREFIX_REGEXP = "^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}\\s+";

    private List<String> origRecords;
    private int wordPosition;

    public LogRecordGroup(List<String> origRecords, int wordPosition) {
        this.origRecords = origRecords;
        this.wordPosition = wordPosition;
    }

    public List<String> getOrigRecords() {
        return origRecords;
    }

    public List<String> getDifferentWords() {
        return origRecords.stream().map(s -> s.replaceFirst(LOG_PREFIX_REGEXP, "").split("\\s+")[wordPosition]).collect(Collectors.toList());
    }
}
