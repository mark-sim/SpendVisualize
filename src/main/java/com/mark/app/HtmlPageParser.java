package com.mark.app;

//This class is designed to parse easyweb.td.com when logged in.
//The goal is to download the .csv file from the banking account to a designated folder.

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.*;

public class HtmlPageParser {

    private static final String FRAME_ELEMENT = "tddetails";
    private static final String TD_PREFIX = "TD UNLIMITED";
    private static final String EACH_COLUMN_XPATH = "td";
    private static final String TABLE_XPATH = "//table[contains(@class,'td-table')]//tbody/tr";
    private static final int TD_NUM = 3;

    public static List<Metadata> transactionsPage(HtmlPage page) throws Exception {
        HtmlPage pageWithCSV = (HtmlPage) page.getFrameByName(FRAME_ELEMENT).getEnclosedPage();
        List<HtmlAnchor> htmlAnchors = pageWithCSV.getAnchors();

        System.out.println("Retrieving Account Anchor...");

        HtmlPage pageWithTransaction = null;
        for (HtmlAnchor htmlAnchor : htmlAnchors) {
            if (htmlAnchor.getTextContent().contains(TD_PREFIX)) {
                System.out.println("Checking transaction for the following account:");
                System.out.println(htmlAnchor.getTextContent());
                pageWithTransaction = htmlAnchor.click();
                break;
            }
        }

        return parse(pageWithTransaction);

    }

    private static List<Metadata> parse(HtmlPage pageWithTransaction) {
        List<HtmlElement> trTags = pageWithTransaction.getByXPath(TABLE_XPATH);
        List<Metadata> metadatas = new ArrayList<Metadata>();
        Double amount = 0.0;
        //Removes the total earnings and cost for that month. If you want to show this later, simply remove the below line
        //and parse it accordingly.
        trTags.remove(trTags.size() - 1);
        //Removes the descriptionHeader. Now trTags only contains transaction data.
        trTags.remove(0);
        for (HtmlElement trTag : trTags) {
            List<HtmlElement> tdTags = trTag.getByXPath(EACH_COLUMN_XPATH);
            if(tdTags.size() >= TD_NUM) {
                //Date
                String date = tdTags.get(0).getTextContent().trim();
                //Name
                String name = tdTags.get(1).getTextContent().trim();
                //Type and prefix name
                String typeAndPrefixName[] = Configuration.check(name).split("=");
                String type = typeAndPrefixName[0];
                String prefixName = typeAndPrefixName[1];

                //amount spent
                String amountStr = tdTags.get(2).getTextContent().trim();
                if (!amountStr.equals("")) {
                    amount = Double.parseDouble(amountStr);
                    Metadata metadata = new Metadata(calendarFormat(date), name, type, amount, prefixName);
                    metadatas.add(metadata);
                }
            }
        }
        Configuration.writeBack();
        return metadatas;
    }

    private static Calendar calendarFormat(String date) {
        //date is in the form of Oct 31, 2017
        String[] dateArr = date.split(" ");
        StringBuilder dayWithComma = new StringBuilder(dateArr[1]);
        dayWithComma.deleteCharAt(dayWithComma.length()-1);
        int day = Integer.parseInt(dayWithComma.toString());
        int year = Integer.parseInt(dateArr[2].trim());
        int month = convertStrToInt(dateArr[0].trim());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        return calendar;
    }

    private static int convertStrToInt(String month) {
        // Use switch statement. Not supported on my machine... :(
        if(month.equals("Jan")){
            return 0;
        } else if(month.equals("Feb")){
            return 1;
        } else if(month.equals("Mar")){
            return 2;
        } else if(month.equals("Apr")){
            return 3;
        } else if(month.equals("May")){
            return 4;
        } else if(month.equals("Jun")){
            return 5;
        } else if(month.equals("Jul")){
            return 6;
        } else if(month.equals("Aug")){
            return 7;
        } else if(month.equals("Sep")){
            return 8;
        } else if(month.equals("Oct")){
            return 9;
        } else if(month.equals("Nov")){
            return 10;
        } else if(month.equals("Dec")){
            return 11;
        } else {
            return -1;
        }
    }
}


