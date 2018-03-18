package io.zipcoder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {

    private Integer countExceptionsThrown = 0;

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    public ArrayList<String> parseRawDataIntoStringArray(String rawData) {
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawData);
        return response;
    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem) {
        String stringPattern = "[@|^|*|%|!|;]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawItem);
        return response;
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException {
        String name = checkName(rawItem);
        Double price = Double.valueOf(checkPrice(rawItem));
        String type = checkType(rawItem);
        String expiration = checkExpiration(rawItem);

        return new Item(name, price, type, expiration);
    }

    public ArrayList<Item> createItemArrayList(String rawData) throws ItemParseException{
        ArrayList<String> temp = parseRawDataIntoStringArray(rawData);
        ArrayList<Item> itemArrayList = new ArrayList<Item>();

        for (int i = 0; i <temp.size() ; i++) {
            itemArrayList.add(parseStringIntoItem(temp.get(i)));
        }
        return itemArrayList;
    }

    public ArrayList<Item> filterItemArrayList(ArrayList<Item> input, String filterType){
        ArrayList<Item> filterItemArrayList = new ArrayList<Item>();
        for (int i = 0; i <input.size() ; i++) {
            if(input.get(i).getName().equals(filterType))
            filterItemArrayList.add(input.get(i));
        }
        return filterItemArrayList;
    }

    public Map<Double, Integer> individualItemCount(ArrayList<Item> filteredArrayList) {
        Map<Double, Integer> priceTotals = new TreeMap<Double, Integer>(Collections.reverseOrder());
        for (int i = 0; i < filteredArrayList.size(); i++) {
            Double key = filteredArrayList.get(i).getPrice();
            Integer count = priceTotals.get(key);
            if (count == null) {
                priceTotals.put(key, 1);
            } else {
                priceTotals.put(key, count + 1);
            }
        }
        return priceTotals;
    }

    public Map<Double, Integer> itemTypeMapWithCounts(String rawData, String filterType) throws ItemParseException {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        ArrayList<Item> filterItemArrayList = filterItemArrayList(itemArrayList, filterType);
        Map<Double, Integer> priceTotals = individualItemCount(filterItemArrayList);
        return priceTotals;
    }

    public Integer totalTimesItemSeen(String rawData, String filterType) throws ItemParseException {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        ArrayList<Item> filterItemArrayList = filterItemArrayList(itemArrayList, filterType);
        return filterItemArrayList.size();
    }

    public HashSet<String> filterTypeArrayList(String rawData) throws ItemParseException {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        HashSet<String> itemHashSet = new HashSet<>();

        for(int i = 0; i < itemArrayList.size(); i++) {
            if (!itemHashSet.contains(itemArrayList.get(i).getName())) {
                itemHashSet.add(itemArrayList.get(i).getName());
            }
        }
        return itemHashSet;
    }
    public String checkName(String input) throws ItemParseException {
        String newInput = fixCookie(input);
        Pattern patternName = Pattern.compile("([Nn]..[Ee]:)(\\w+)");
        Matcher matcherName= patternName.matcher(newInput);

        if (matcherName.find())
            return matcherName.group(2).toLowerCase();
        else throw new ItemParseException();
    }

    public String fixCookie(String input){
        Pattern patternCookies = Pattern.compile("([Cc][0Oo][0Oo][Kk][Ii][Ee][Ss])");
        Matcher matcherCookie= patternCookies.matcher(input);
        return matcherCookie.replaceAll("cookies");
    }

    public String checkPrice(String input) throws ItemParseException{
        Pattern patternPrice = Pattern.compile("([Pp]...[Ee]:)(\\d\\.\\d{2})");
        Matcher matcherPrice= patternPrice.matcher(input);

        if (matcherPrice.find())
            return matcherPrice.group(2);
        else throw new ItemParseException();
    }

    public String checkType(String input) throws ItemParseException{
        Pattern patternType = Pattern.compile("([Tt]..[Ee]:)(\\w+)");
        Matcher matcherType = patternType.matcher(input);

        if (matcherType.find())
            return matcherType.group(2).toLowerCase();
        else throw new ItemParseException();
    }

    public String checkExpiration(String input) throws ItemParseException{
        Pattern patternExpiration = Pattern.compile("([Ee]........[Nn]:)(\\d\\/\\d{2}\\/\\d{4})");
        Matcher matcherExpiration = patternExpiration.matcher(input);

        if (matcherExpiration.find())
            return matcherExpiration.group(2);
        else throw new ItemParseException();
    }

    public Integer getExceptionsThrown(String rawData)  {

        ArrayList<String> parsedRawData = parseRawDataIntoStringArray(rawData);
        for (int i = 0; i < parsedRawData.size(); i++)
            try {
                parseStringIntoItem(parsedRawData.get(i));
            } catch (ItemParseException e){
                countExceptionsThrown++;
            }
        return this.countExceptionsThrown;
    }
}


//    public String checkName(String input) throws ItemParseException{
//        Pattern patternName = Pattern.compile("([Nn]..[Ee]:)(\\w+)");
//        Matcher matcherName= patternName.matcher(input);
//
//        if (matcherName.find())
//            return matcherName.group(2).toLowerCase();
//        else throw new ItemParseException();
//    }

//    Pattern[] patternName = new Pattern[4];
//        patternName[0] = Pattern.compile("([Nn]..[Ee]:)([Mm]..[Kk])");
//                patternName[1] = Pattern.compile("([Nn]..[Ee]:)([Bb]...[Dd])");
//                patternName[2] = Pattern.compile("([Nn]..[Ee]:)([Cc].....[Ss])");
//                patternName[3] = Pattern.compile("([Nn]..[Ee]:)([Aa]....[Ss])");
//
//                for (int i = 0; i < patternName.length; i++) {
//        Matcher nameMatcher = patternName[i].matcher(input);
//        if (nameMatcher.find()) {
//        return "milk";
//        } else if (nameMatcher.find()) {
//        return "bread";
//        } else if (nameMatcher.find()) {
//        return "cookies";
//        } else if (nameMatcher.find()) {
//        return "apples";
//        }
//        }
//        throw new ItemParseException();