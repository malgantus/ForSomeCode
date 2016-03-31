package ru.olkhovikdenis.olkhovikdenisapp;



import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**

 */

public class News {

    public String title = null;
    public String link = null;
    public String description = null;

    public static List<News> SAXParsing(String xmlStr){

        SAXParserFactory factory = SAXParserFactory.newInstance();

        List<News> ListNews = new ArrayList<News>();

        try {

            SAXParser saxParser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler();
            InputSource is = new InputSource(new StringReader(xmlStr));
            is.setEncoding("UTF-8");
            saxParser.parse(is, handler);
            for(News driver : handler.listOfNews){
                ListNews.add(driver);
            }

        }  catch (Throwable err) {
            err.printStackTrace ();
        }
        return ListNews;
    }


    //class SaxHandler
    public static class SaxHandler extends DefaultHandler {

        public List<News> listOfNews = new ArrayList<News>();

        private Stack<String> elementStack = new Stack<String>();
        private Stack<Object> objectStack = new Stack<Object>();


        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) throws SAXException {

            super.startElement(uri, localName, qName, attributes);
            this.elementStack.push(qName);

            if ("item".equals(qName)) {
                News news = new News();
                this.objectStack.push(news);
                this.listOfNews.add(news);
            }

        }


        public void endElement(String uri, String localName,
                               String qName) throws SAXException {

            this.elementStack.pop();
            if ("item".equals(qName)) {
                this.objectStack.pop();
            }

        }

        public void characters(char ch[], int start, int length)
                throws SAXException {

            String value = new String(ch, start, length).trim();
           // if (value.length() == 0) return; // ignore white space

            if ("title".equals(currentElement())) {

                if (this.objectStack.size() != 0) {
                    News news = (News) this.objectStack.peek();
                    news.title = value;
                }

            } else if ("link".equals(currentElement())) {

                if (this.objectStack.size() != 0) {
                    News news = (News) this.objectStack.peek();
                    news.link = value;
                }
            } else if ("description".equals(currentElement())) {

                if (this.objectStack.size() != 0) {
                    News news = (News) this.objectStack.peek();
                    news.description = value;
                }
            }
        }

        private String currentElement() {
            return this.elementStack.peek();
        }

    }
}