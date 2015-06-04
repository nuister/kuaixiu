package com.gsh.base.alipay;

/**
 *@author Tan Chunmao
 */
public class PayItem {
    private String title;
    private String summary;
    private double price;

    public PayItem(String title, String summary, double price) {
        this.title = title;
        this.summary = summary;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public double getPrice() {
        return price;
    }
}
