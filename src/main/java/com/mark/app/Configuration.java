package com.mark.app;

import org.apache.commons.codec.CharEncoding;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    // configFile must have the following properties :
    // <Key:prefixName of the company> = <Value:Type of the company> one for each line.
    private static String configFile;
    private static List<CompanyType> companyTypes = new ArrayList<CompanyType>();

    public Configuration(String configFile) {
        this.configFile = configFile;
        moveFileToList();
    }

    private void moveFileToList() {
        FileReader fr = null;
        BufferedReader br = null;
        try  {
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null) {
                String[] kvp = line.split("=");
                CompanyType companyType = new CompanyType(kvp[0].trim().toUpperCase(), kvp[1].trim().toUpperCase());
                companyTypes.add(companyType);
            }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
                if(fr != null) {
                    fr.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    //returns type of company("fullName") and prefixName specified by the config file.
    //They are separated by equals sign, which must be split later.
    public static String check(String fullName) {
        int length = companyTypes.size();
        for(int i=0; i<length; i++) {
            CompanyType ct = companyTypes.get(i);
            String prefixName = ct.getPrefixName();
            if(fullName.contains(prefixName)) {
                transposeHeuristic(i);
                return ct.getType() + "=" + prefixName;
            }
        }
        return "UNKNOWN=UNKNOWN";
    }

    public static void writeBack() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(configFile, CharEncoding.UTF_8);
            for(CompanyType ct : companyTypes) {
                pw.println(ct.getPrefixName() + "="  + ct.getType());
            }
            System.out.println("Successfully self-organized: " + configFile);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(pw != null) {
                pw.close();
            }
        }
    }
    private static void transposeHeuristic(int num) {
        // if index is at 0, it cannot swap with previous element.
        if(num == 0) return;
        CompanyType temp = companyTypes.get(num-1);
        companyTypes.set(num-1, companyTypes.get(num));
        companyTypes.set(num,temp);
    }


}
