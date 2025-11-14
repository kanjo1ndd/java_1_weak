package com.example;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class XmlWriter {
    public void writeStatistics(File outFile, Map<String, Long> data) throws Exception {
        try (FileWriter fw = new FileWriter(outFile)) {
            fw.write("<statistics>\n");
            for (Map.Entry<String, Long> e : data.entrySet()) {
                fw.write("  <item>\n");
                fw.write("    <value>" + escapeXml(e.getKey()) + "</value>\n");
                fw.write("    <count>" + e.getValue() + "</count>\n");
                fw.write("  </item>\n");
            }
            fw.write("</statistics>\n");
        }
    }
    private String escapeXml(String s) {
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&apos;");
    }
}