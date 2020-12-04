package model;


import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class PositionXMLFormatter  {

    public Element createRootElement(Document doc) {
        return doc.createElement("position");
    }

    public Element createElementFromRecord(String record, Document doc) {
        Element position = doc.createElement("position");


        return position;
    }


}
