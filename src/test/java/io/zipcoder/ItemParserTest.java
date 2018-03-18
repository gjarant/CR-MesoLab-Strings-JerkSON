package io.zipcoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ItemParserTest {

    private String rawSingleItem =    "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawSingleItemIrregularSeperatorSample = "naMe:MiLK;price:3.23;type:Food^expiration:1/11/2016##";

    private String rawBrokenSingleItem =    "naMe:;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawMultipleItems = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
                                      +"naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##"
                                      +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##";
    private String rawMultipleItems2 = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
                                    +"naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##"
                                    +"NAMe:BrEAD;price:3.23;type:Food;expiration:2/25/2016##"
                                    +"NAMe:BrEAD;price:3.23;type:Food;expiration:2/25/2016##"
                                    +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##"
                                    +"NAMe:BrEAD;price:2.23;type:Food;expiration:2/25/2016##";

    private String rawMultipleItemsBroken = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
            +"naME:;price:1.23;type:Food;expiration:1/02/2016##"
            +"NAMe:BrEAD;price:3.23;type:Food;expiration:2/25/2016##"
            +"NAMe:BrEAD;price:;type:Food;expiration:2/25/2016##"
            +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##"
            +"NAMe:BrEAD;price:2.23;type:Food;expiration:##";

    private ItemParser itemParser;

    @Before
    public void setUp(){
        itemParser = new ItemParser();
    }

    @Test
    public void parseRawDataIntoStringArrayTest(){
        Integer expectedArraySize = 3;
        ArrayList<String> items = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        Integer actualArraySize = items.size();
        assertEquals(expectedArraySize, actualArraySize);
    }

    @Test
    public void parseStringIntoItemTest() throws ItemParseException{
        Item expected = new Item("milk", 3.23, "food","1/25/2016");
        Item actual = itemParser.parseStringIntoItem(rawSingleItem);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = ItemParseException.class)
    public void parseBrokenStringIntoItemTest() throws ItemParseException{
        itemParser.parseStringIntoItem(rawBrokenSingleItem);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTest(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItem).size();
        assertEquals(expected, actual);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTestIrregular(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItemIrregularSeperatorSample).size();
        assertEquals(expected, actual);
    }

    @Test
    public void createItemArrayListTest() throws ItemParseException {
        String expected ="name:milk price:3.23 type:food expiration:1/25/2016";
        String actual  = itemParser.createItemArrayList(rawMultipleItems).get(0).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void filterItemArrayListTest() throws ItemParseException {
        ArrayList<Item> filterItemArrayListTester = itemParser.createItemArrayList(rawMultipleItems);
        String expected ="[name:bread price:1.23 type:food expiration:1/02/2016, name:bread price:1.23 type:food expiration:2/25/2016]";
        String actual  = itemParser.filterItemArrayList(filterItemArrayListTester,"bread").toString();

        assertEquals(expected, actual);
    }

    @Test
    public void individualItemCountTest1() throws ItemParseException {
        ArrayList<Item> individualItemCountTester = itemParser.filterItemArrayList(itemParser.createItemArrayList(rawMultipleItems),"bread");
        String expected ="{1.23=2}";
        String actual  = itemParser.individualItemCount(individualItemCountTester).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void individualItemCountTest2() throws ItemParseException {
        ArrayList<Item> individualItemCountTester = itemParser.filterItemArrayList(itemParser.createItemArrayList(rawMultipleItems2),"bread");
        String expected ="{3.23=2, 2.23=1, 1.23=2}";
        String actual  = itemParser.individualItemCount(individualItemCountTester).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void itemTypeMapWithCountsTest() throws ItemParseException {
        String expected ="{3.23=2, 2.23=1, 1.23=2}";
        String actual  = itemParser.itemTypeMapWithCounts(rawMultipleItems2,"bread").toString();
        assertEquals(expected, actual);
    }

    @Test
    public void totalTimesItemSeen() throws ItemParseException {
        Integer expected = 5;
        Integer actual  = itemParser.totalTimesItemSeen(rawMultipleItems2, "bread");
        assertEquals(expected, actual);
    }

    @Test
    public void filterTypeArrayListTest() throws ItemParseException {
        String expected = "[bread, milk]";
        String actual = itemParser.filterTypeArrayList(rawMultipleItems2).toString();
        assertEquals(expected, actual);
    }



    @Test
    public void checkNameTest1() throws ItemParseException{
        String rawItemTest = "naMe:c00kies;price:3.23;type:Food;expiration:1/25/2016##";
        String expected = "cookies";
        String  actual = itemParser.checkName(rawItemTest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkNameTest2() throws ItemParseException{
        String rawItemTest = "naMe:mIlk;price:3.23;type:Food;expiration:1/25/2016##";
        String expected = "milk";
        String  actual = itemParser.checkName(rawItemTest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkPriceTest() throws ItemParseException{
        String rawItemTest = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";
        String expected = "3.23";
        String  actual = itemParser.checkPrice(rawItemTest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkTypeTest() throws ItemParseException{
        String rawItemTest = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";
        String expected = "food";
        String  actual = itemParser.checkType(rawItemTest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkExpirationTest() throws ItemParseException{
        String rawItemTest = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";
        String expected = "1/25/2016";
        String  actual = itemParser.checkExpiration(rawItemTest);
        assertEquals(expected, actual);
    }

    @Test
    public void getExceptionsThrownTest() throws ItemParseException{
        Integer expected = 3;
        Integer  actual = itemParser.getExceptionsThrown(rawMultipleItemsBroken);
        assertEquals(expected, actual);
    }
}
