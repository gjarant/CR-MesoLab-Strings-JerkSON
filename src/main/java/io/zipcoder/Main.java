package io.zipcoder;

import org.apache.commons.io.IOUtils;


public class Main {

    public String readRawDataToString() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void main(String[] args) throws Exception{
        String output = (new Main()).readRawDataToString();
        ItemParser item = new ItemParser();
        ItemParseException itemParseException = new ItemParseException();
        System.out.println(item.formatText(output));
        // TODO: parse the data in output into items, and display to console.
    }
}
