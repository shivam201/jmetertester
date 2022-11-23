package org;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class genericfun {
    public static String ReadCSV(String File1, String FieldName) {
        String Results = "";

        try {
            LineIterator it = FileUtils.lineIterator(new File(File1), "UTF-8");
            String line = it.nextLine();
            ;
            int ResponseCode_Index = 0;

            // Finding the Index or ResponseCode//
            String splitter[] = line.split(",");
            for (int i = 0; i < splitter.length; i++) {
                ResponseCode_Index = ResponseCode_Index + 1;
                if (splitter[i].equalsIgnoreCase(FieldName)) {
                    // System.out.println("Response Code Column found");
                    break;
                }
            }

            while (it.hasNext()) {
                String line1 = it.nextLine();
                Results = Results + Value_at_Index(line1, ResponseCode_Index).trim() + " ";
            }
            // System.out.println("Results = "+Results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // LineIterator.closeQuietly(it);
        }
        return Results;
    }

    public static String Value_at_Index(String line, int index) {
        String splitter[] = line.split(",");
        return splitter[index - 1];
    }
}
