package io.zipcoder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {

    private Integer countExceptionsThrown = 0;
    private Integer count = 0;

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
            if (checkName(rawItem) == null || checkPrice(rawItem) == null || checkType(rawItem) == null
                    || checkExpiration(rawItem) == null) {
                throw new ItemParseException();
            }

            String name = checkName(rawItem);
            Double price = Double.valueOf(checkPrice(rawItem));
            String type = checkType(rawItem);
            String expiration = checkExpiration(rawItem);

            return new Item(name, price, type, expiration);
    }

    public ArrayList<Item> createItemArrayList(String rawData) {
        ArrayList<String> temp = parseRawDataIntoStringArray(rawData);
        ArrayList<Item> itemArrayList = new ArrayList<Item>();

        for (int i = 0; i <temp.size() ; i++) {
            try {
                itemArrayList.add(parseStringIntoItem(temp.get(i)));
            } catch (ItemParseException e) {
                count++;
            }
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

    public Map<Double, Integer> itemTypeMapWithCounts(String rawData, String filterType)  {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        ArrayList<Item> filterItemArrayList = filterItemArrayList(itemArrayList, filterType);
        Map<Double, Integer> priceTotals = individualItemCount(filterItemArrayList);
        return priceTotals;
    }

    public Integer totalTimesItemSeen(String rawData, String filterType)  {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        ArrayList<Item> filterItemArrayList = filterItemArrayList(itemArrayList, filterType);
        return filterItemArrayList.size();
    }

    public ArrayList<String> filterTypeArrayList(String rawData)  {
        ArrayList<Item> itemArrayList = createItemArrayList(rawData);
        ArrayList<String> filterType = new ArrayList<String>();

        for(int i = 0; i < itemArrayList.size(); i++) {
            if (!filterType.contains(itemArrayList.get(i).getName())) {
                filterType.add(itemArrayList.get(i).getName());
            }
        }
        return filterType;
    }

    public String formatText(String rawData) throws ItemParseException {
        ArrayList<String> filterType = filterTypeArrayList(rawData);
        StringBuilder sb = new StringBuilder();

            for (int i = 0; i < filterType.size(); i++) {
                String name = filterType.get(i);
                Integer total = totalTimesItemSeen(rawData, filterType.get(i));
                String priceFormated = formatPriceField(rawData, filterType.get(i));
                sb.append("name: ");
                sb.append(String.format("%7s", name.substring(0, 1).toUpperCase() + name.substring(1)));
                sb.append("\t \t");
                sb.append(" seen: " + total + " times\n");
                sb.append("============= \t \t =============\n");
                sb.append(priceFormated + "\n");
            }

        sb.append("Errors              " + " seen: " + getExceptionsThrown(rawData) + " times");

        return sb.toString();
    }

    public String formatPriceField(String rawData, String filterType)  {
        Map<Double, Integer> priceTotals = itemTypeMapWithCounts(rawData, filterType);
        StringBuilder sb = new StringBuilder();
        Set mapSet = (Set) priceTotals.entrySet();
        Iterator mapIterator = mapSet.iterator();
        while (mapIterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
            // getKey Method of HashMap access a key of map
            Object keyValue = mapEntry.getKey();
            //getValue method returns corresponding key's value
            Object value = mapEntry.getValue();
            String time = (1 < (Integer)value) ?  " times": " time";
            sb.append("Price:   " + keyValue + "\t\t seen: " + value + time + "\n");
            sb.append("-------------\t\t -------------\n");
        }
        return sb.toString();
    }

    public String checkName(String input) {
        String newInput = fixCookie(input);
        Pattern patternName = Pattern.compile("([Nn]..[Ee]:)(\\w+)");
        Matcher matcherName= patternName.matcher(newInput);

        if (matcherName.find())
            return matcherName.group(2).toLowerCase();
        else return null;
    }

    public String fixCookie(String input){
        Pattern patternCookies = Pattern.compile("([Cc][0Oo][0Oo][Kk][Ii][Ee][Ss])");
        Matcher matcherCookie= patternCookies.matcher(input);
        return matcherCookie.replaceAll("cookies");
    }

    public String checkPrice(String input) {
        Pattern patternPrice = Pattern.compile("([Pp]...[Ee]:)(\\d\\.\\d{2})");
        Matcher matcherPrice= patternPrice.matcher(input);

        if (matcherPrice.find())
            return matcherPrice.group(2);
        else return null;
    }

    public String checkType(String input) {
        Pattern patternType = Pattern.compile("([Tt]..[Ee]:)(\\w+)");
        Matcher matcherType = patternType.matcher(input);

        if (matcherType.find())
            return matcherType.group(2).toLowerCase();
        else return null;
    }

    public String checkExpiration(String input) {
        Pattern patternExpiration = Pattern.compile("([Ee]........[Nn]:)(\\d\\/\\d{2}\\/\\d{4})");
        Matcher matcherExpiration = patternExpiration.matcher(input);

        if (matcherExpiration.find())
            return matcherExpiration.group(2);
        else return null;
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