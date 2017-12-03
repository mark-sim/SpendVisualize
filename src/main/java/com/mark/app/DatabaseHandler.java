package com.mark.app;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {
    final private static String TRANSACTION_QUERY = "INSERT INTO [MyDebitCard].[dbo].[Transaction] VALUES (?, ?, ?, ?, ?)";
    final private static String TYPE_AMOUNT_QUERY = "INSERT INTO [MyDebitCard].[dbo].[TypeAmount] VALUES (?, ?)";
    final private static String DELETE_TYPE_AMOUNT = "DELETE FROM [MyDebitCard].[dbo].[TypeAmount]";
    final private static String DELETE_TRANSACTION = "DELETE FROM [MyDebitCard].[dbo].[Transaction]";
    private static Map<String, Double> typeAmount = new HashMap<String,Double>();

    public static void insertDB(List<Metadata> metadatas) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=MyDebitCard;integratedSecurity=true";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to database.");

            deleteData(conn);

            System.out.println("Inserting transactions...");
            for(Metadata m : metadatas) {
                ps = createTransactionInsertionStatement(m, conn);
                ps.executeUpdate();
            }
            System.out.println("Inserted transactions...");

            for(String type : typeAmount.keySet()) {
                ps = createTypeAmountInsertionStatement(type, conn);
                ps.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }

    private static PreparedStatement createTransactionInsertionStatement(Metadata m, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(TRANSACTION_QUERY);
        DateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String type = m.getType();
        String name = m.getName();
        Double amount = m.getAmount();
        String date = Metadata.formatDate(m.getDate());
        String updatedTime = dateFormat.format(Calendar.getInstance().getTime());
        calculateTypeAmount(type,amount);
        ps.setString(1,type);
        ps.setString(2,name);
        ps.setDouble(3,amount);
        ps.setString(4,date);
        ps.setString(5,updatedTime);
        return ps;
    }

    private static PreparedStatement createTypeAmountInsertionStatement(String type, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(TYPE_AMOUNT_QUERY);
        Double totalAmount = typeAmount.get(type);
        ps.setString(1,type);
        ps.setDouble(2,totalAmount);
        return ps;
    }

    private static void calculateTypeAmount(String type, Double amount) {
        Double value = 0.0;
        if(typeAmount.get(type) != null) {
            value = typeAmount.get(type);
        }
        typeAmount.put(type, amount + value);
    }

    private static void deleteData(Connection conn) throws Exception {
        PreparedStatement ps = null;
        ps = conn.prepareStatement(DELETE_TYPE_AMOUNT);
        ps.executeUpdate();
        ps = conn.prepareStatement(DELETE_TRANSACTION);
        ps.executeUpdate();
        DbUtils.closeQuietly(ps);
    }

}
