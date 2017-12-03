package com.mark.app;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class Authenticate {
    
    private static String URL_TO_LOGIN = "https://easyweb.td.com";
    private static String accessCardNo;
    private static String password;
    private static HtmlPage pageToBeParsed;

    public Authenticate(String accessCardNo, String password) {
        this.accessCardNo = accessCardNo;
        this.password = password;
    }

    public Boolean connect(WebClient webClient) {
        pageToBeParsed = null;
        try {
            final HtmlPage page1 = webClient.getPage(URL_TO_LOGIN);
            final HtmlForm form = page1.getFormByName("login");

            final HtmlSubmitInput button = form.getInputByValue("Login");

            final HtmlTextInput textField = form.getInputByName("login:AccessCard");
            textField.setValueAttribute(accessCardNo);
            final HtmlPasswordInput textField2 = form.getInputByName("login:Webpassword");
            textField2.setValueAttribute(password);

            System.out.println("Logging into: " + URL_TO_LOGIN);
            pageToBeParsed = button.click();
            System.out.println("Logged into: " + pageToBeParsed.getBaseURI());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return (pageToBeParsed != null);
    }

    public HtmlPage getPageToBeParsed() {
        return pageToBeParsed;
    }
}
