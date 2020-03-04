package com.lmabbe.core;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;

public class XmlParserHandler {

    private Configuration.ConfigurationBuilder configurationBuilder;
    private InputStream configInputStream;

    private XmlParserHandler() {

    }

    public XmlParserHandler(String path) {
        configurationBuilder = new Configuration.ConfigurationBuilder();
        configInputStream = this.getClass().getResourceAsStream(path);
    }

    public Configuration parser() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = null;
            try {
                document = builder.parse(configInputStream);
                configurationBuilder
                        //公共
                        .application(document.getElementsByTagName("application").item(0).getAttributes().getNamedItem("name").getNodeValue())
                        .registry(document.getElementsByTagName("registry").item(0).getAttributes().getNamedItem("address").getNodeValue());
                
                if (document.getElementsByTagName("reference").getLength() == 0) {
                    configurationBuilder.protocol(new Configuration.Protocol(document.getElementsByTagName("protocol").item(0).getAttributes().getNamedItem("name").getNodeValue(),
                            Integer.parseInt(document.getElementsByTagName("protocol").item(0).getAttributes().getNamedItem("port").getNodeValue())))
                            .service(new Configuration.Service(document.getElementsByTagName("service").item(0).getAttributes().getNamedItem("interface").getNodeValue(),
                                    document.getElementsByTagName("service").item(0).getAttributes().getNamedItem("ref").getNodeValue()))
                            .bean(new Configuration.Bean(document.getElementsByTagName("bean").item(0).getAttributes().getNamedItem("id").getNodeValue(),
                                    document.getElementsByTagName("bean").item(0).getAttributes().getNamedItem("class").getNodeValue()));
                } else {
                    configurationBuilder.reference(new Configuration.Reference(document.getElementsByTagName("reference").item(0).getAttributes().getNamedItem("id").getNodeValue(),
                            document.getElementsByTagName("reference").item(0).getAttributes().getNamedItem("interface").getNodeValue()));
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return configurationBuilder.build();
    }
}
