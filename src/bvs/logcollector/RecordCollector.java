package bvs.logcollector;

import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import static bvs.logcollector.LogRecordGroup.LOG_PREFIX_REGEXP;
import static java.lang.System.exit;

public class RecordCollector {
    Map<String, LogRecordGroup> recordCollection = new HashMap<>();

    public RecordCollector() {
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("LogRecordGrouper", options);
            exit(1);
            return;
        }

        String inputFile = cmd.getOptionValue("input");
        String outputFile = cmd.getOptionValue("output");

        RecordCollector collector = new RecordCollector();
        try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {
            stream.forEach(collector::processRecord);
        } catch (IOException e) {
            e.printStackTrace();
        }
        collector.writeResults(outputFile);
        System.out.println("Result data is written to file " + outputFile);
    }

    private void processRecord(String str) {
        String payload = str.replaceFirst(LOG_PREFIX_REGEXP, "");
        String[] words = payload.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Use special symbol instead word which can be changed
            words[i] = "*";
            String key = String.join(" ", words);
            LogRecordGroup record = recordCollection.get(key);
            if (record != null) {
                record.getDifferentWords().add(word);
                record.getOrigRecords().add(str);
            } else {
                record = new LogRecordGroup(new LinkedList<>(Arrays.asList(str)), i);
            }
            recordCollection.put(key, record);
            words[i] = word;
        }
    }

    private void writeResults(String outputFile) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            for (Map.Entry<String, LogRecordGroup> record : recordCollection.entrySet()) {
                if (record.getValue().getDifferentWords().size() > 1) {
                    writeGroup(record.getValue(), writer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeGroup(LogRecordGroup group, BufferedWriter writer) throws IOException {
        group.getOrigRecords().forEach(s -> {
            try {
                writer.write(s);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.write("The changing word was: " + String.join(", ", group.getDifferentWords()));
        writer.newLine();
        writer.newLine();
    }

}
