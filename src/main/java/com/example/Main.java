package com.example;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java Main <folder_path> <attribute>");
            return;
        }

        String folderPath = args[0];
        String attribute = args[1];

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return;
        }

        File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            System.err.println("No JSON files found in folder: " + folderPath);
            return;
        }

        StatisticsAggregator aggregator = new StatisticsAggregator();

        int threadCount = Math.min(jsonFiles.length, Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (File f : jsonFiles) {
            JsonFileParser parser = new JsonFileParser();
            parser.parseFile(f, book -> aggregator.accept(book, attribute));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(50);
        }

        File outFile = new File("target/statistics_by_" + attribute + ".xml");
        writeXml(aggregator, outFile);

        System.out.println("Statistics saved to: " + outFile.getAbsolutePath());
    }

    private static void writeXml(StatisticsAggregator aggregator, File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("statistics");
        doc.appendChild(root);

        aggregator.snapshotSortedDesc().forEach((value, count) -> {
            Element item = doc.createElement("item");

            Element val = doc.createElement("value");
            val.setTextContent(value);
            item.appendChild(val);

            Element c = doc.createElement("count");
            c.setTextContent(String.valueOf(count));
            item.appendChild(c);

            root.appendChild(item);
        });

        try (FileWriter writer = new FileWriter(file)) {
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new javax.xml.transform.dom.DOMSource(doc),
                    new javax.xml.transform.stream.StreamResult(writer));
        }
    }
}