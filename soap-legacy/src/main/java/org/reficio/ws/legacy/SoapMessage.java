package org.reficio.ws.legacy;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.reficio.ws.legacy.XmlUtils;

public class SoapMessage {
	XmlObject message;
	
	public SoapMessage(String content) {
		try {
			this.message = XmlObject.Factory.parse(content);
		} catch (XmlException e) {
			e.printStackTrace();
		}
	}
	
	public SoapMessage(XmlObject content) {
		this.message = content;
	}
	
	public void setParam(String name, String value) {
		String xpath = "//*:" + name;
		
        String namespaces = XmlUtils.declareXPathNamespaces(this.message);
        if (namespaces != null && namespaces.trim().length() > 0)
            xpath = namespaces + xpath;
        
        XmlObject[] path = this.message.selectPath(xpath);
        for (XmlObject xml : path) {
            XmlUtils.setNodeValue(xml.getDomNode(), value);
        }
	}
	
	public String toString() {
		return this.message.toString();
	}
}
