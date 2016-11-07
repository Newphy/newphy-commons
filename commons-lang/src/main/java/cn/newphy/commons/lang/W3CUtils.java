package cn.newphy.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class W3CUtils {
	private static XPath XPATH = XPathFactory.newInstance().newXPath();

	/**
	 * 新建document
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		document.setXmlVersion("1.0");
		return document;
	}

	/**
	 * 获得根节点
	 * 
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static Node getRootNode(String xml) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));
		Element root = doc.getDocumentElement();
		return root;
	}

	/**
	 * 获得根节点
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static Node getRootNode(InputStream inputStream) throws SAXException, IOException,
			ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(inputStream);
		Element root = doc.getDocumentElement();
		return root;
	}

	/**
	 * 根据XPath查找节点
	 * 
	 * @param xpath
	 * @param parentNode
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Node findNode(String xPath, Node parentNode) throws XPathExpressionException {
		return (Node) XPATH.evaluate(xPath, parentNode, XPathConstants.NODE);
	}
	
	/**
	 * 判断是否存在子节点
	 * 
	 * @param parentNode
	 * @param childNodeName
	 * @return
	 * @throws XPathExpressionException 
	 */
	public static boolean existChildNode(Node parentNode, String childNodeName) throws XPathExpressionException {
		return (findNode("/" +childNodeName, parentNode)) != null;
	}
	
	/**
	 * 增加子节点
	 * @param parentNode
	 * @param childNodeName
	 * @param value
	 */
	public static void appendChildNode(Node parentNode, String childNodeName, String value) {
		 Document doc = parentNode.getOwnerDocument();
		 Element child = doc.createElement(childNodeName);
		 child.setTextContent(value);
		 parentNode.appendChild(child);
	}
	
	
	/**
	 *  输出到outputStream
	 * @param doc
	 * @param output
	 * @throws TransformerException
	 */
	public static void writeDocument(Document doc, OutputStream output) throws TransformerException {
	     TransformerFactory tfactory=TransformerFactory.newInstance();
         Transformer transformer=tfactory.newTransformer();
         DOMSource source=new DOMSource(doc);
         StreamResult result=new StreamResult(output);
         transformer.transform(source,result);
	}
	
}
