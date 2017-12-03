package com.mark.app;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.cli.*;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AnalyzerMain {

    private static final String CMD_LINE_SYNTAX = "SpendVisualize -ac <argument> -p <argument> -cp <argument> [optional: -b <argument>]";

    public static void main(String[] args) throws Exception{
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch(Exception e) {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            return;
        }

        if(cmd.hasOption("help")) {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            return;
        }
        String accessCardNo = cmd.getOptionValue("ac");
        String password = cmd.getOptionValue("p");
        String config_path = cmd.getOptionValue("cp");
        String bv = "";
        if(cmd.hasOption("b")) {
            bv = cmd.getOptionValue("b").toUpperCase();
        }
        //default browserversion is chrome
        BrowserVersion browserVersion = BrowserVersion.CHROME;

        if(bv == "EDGE") {
            browserVersion = BrowserVersion.EDGE;
        } else if (bv == "FIREFOX") {
            browserVersion = BrowserVersion.FIREFOX_52;
        } else if (bv == "IE") {
            browserVersion = BrowserVersion.INTERNET_EXPLORER;
        } else if (bv == "CHROME") {
            browserVersion = BrowserVersion.CHROME;
        }

        List<Metadata> metadatas = new ArrayList<Metadata>();
        turnLoggingOff();
        new Configuration(config_path); // should get it from cmdline argument
        Authenticate auth = new Authenticate(accessCardNo, password); //should get it from cmdline argument
        WebClient webClient = createWebClient(browserVersion);
        if(auth.connect(webClient)) {
            metadatas = HtmlPageParser.transactionsPage(auth.getPageToBeParsed());
            DatabaseHandler.insertDB(metadatas);
            InfluxDBHandler.insertIntoInfluxDB(metadatas);
        }


    }

    private static Options createOptions() {
        Options options = new Options();

        Option accessCardNo = new Option("ac", "AccessCard Number to TD", true, "AccessCard Number to TD EasyWeb Banking");
        accessCardNo.setRequired(true);
        Option password = new Option("p", "Password to TD", true, "Password to TD EasyWeb Banking");
        password.setRequired(true);
        Option configFilePath = new Option("cp", "Config file path", true, "Path to your config file directory");
        configFilePath.setRequired(true);
        Option help = new Option("help", "Description of arguments", false, "Description of arguments");
        Option browserVersion = new Option("b", "BrowserVersion to fetch transaction data", true, "BrowserVersion to fetch transaction data." +
                                                                                                                                    "Currently supports CHROME, IE, EDGE, AND FIREFOX");

        options.addOption(accessCardNo);
        options.addOption(password);
        options.addOption(configFilePath);
        options.addOption(help);
        options.addOption(browserVersion);

        return options;
    }

    private static void turnLoggingOff() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    }

    private static WebClient createWebClient(BrowserVersion browserVersion) {
        final WebClient webClient = new WebClient(browserVersion);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        return webClient;
    }


}
