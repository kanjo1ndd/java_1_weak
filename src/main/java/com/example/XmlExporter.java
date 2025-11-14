package com.example;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class XmlExporter {

    public static void export(Map<String, Long> stats, String attribute) throws Exception {
        File file = new File("statistics_by_" + attribute + ".xml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("<statistics>\n");
            for (Map.Entry<String, Long> entry : stats.entrySet()) {
                writer.write("  <item>\n");
                writer.write("    <value>" + entry.getKey() + "</value>\n");
                writer.write("    <count>" + entry.getValue() + "</count>\n");
                writer.write("  </item>\n");
            }
            writer.write("</statistics>\n");
        }
        System.out.println("XML-файл сохранен: " + file.getAbsolutePath());
    }
}