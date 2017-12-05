package com.mark.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Metadata {
    private Calendar date;
    private String name;
    private String type;
    private Double amount;
    private String prefixName;

    public Metadata(Calendar date, String name, String type, Double amount, String prefixName) {
        this.date = date;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.prefixName = prefixName;
    }

    public Metadata getMetadata() {
        return this;
    }

    public String getPrefixName() {
        return this.prefixName;
    }

    public Double getAmount() {
        return this.amount;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Calendar getDate() {
        return this.date;
    }

    public String toString() {
        return String.format(formatDate(this.date) + ": " + this.name + " - " + this.amount + " (" + this.type + ")");
    }

    public static String formatDate(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }


    // No support for getMonth, getDay, getYear. Simply get the Calendar instance and use that.

}
