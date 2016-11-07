package cn.newphy.commons.lang;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JAXBUtils {

	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @param writer
	 * @return
	 * @throws JAXBException
	 */
	public static String convertToXml(Object obj) throws JAXBException {
		return convertToXml(obj, "UTF-8");
	}

	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 * @throws JAXBException
	 */
	public static String convertToXml(Object obj, String encoding) throws JAXBException {
		if(obj == null) {
			return null;
		}
		String result = null;
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

		StringWriter writer = new StringWriter();
		marshaller.marshal(obj, writer);
		result = writer.toString();

		return result;
	}
	

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertToObject(String xml, Class<T> c) throws JAXBException {
		T t = null;
		JAXBContext context = JAXBContext.newInstance(c);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		t = (T) unmarshaller.unmarshal(new StringReader(xml));
		return t;
	}
	

	

	/**
	 * xml转换成JavaBean
	 * 
	 * @param node
	 * @param type
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertToObject(Node node, Class<T> type) throws JAXBException {
		T t = null;
		JAXBContext context = JAXBContext.newInstance(type);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		t = (T) unmarshaller.unmarshal(node);
		return t;
	}

	/**
	 * 根据XPath进行数据绑定
	 * 
	 * @param xPath
	 * @param parentNode
	 * @param type
	 * @return
	 * @throws JAXBException
	 * @throws XPathExpressionException
	 */
	public static <T> T convertToObjectByXPath(String xPath, Node parentNode, Class<T> type) throws JAXBException,
			XPathExpressionException {
		Node node = W3CUtils.findNode(xPath, parentNode);
		return convertToObject(node, type);
	}

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xmlInputStream
	 * @param c
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertToObject(InputStream xmlInputStream, Class<T> c) throws JAXBException {
		T t = null;
		JAXBContext context = JAXBContext.newInstance(c);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		t = (T) unmarshaller.unmarshal(xmlInputStream);
		return t;
	}
	
	
	/**
	 * 将对象转为Node
	 * @param obj
	 * @param rootName
	 * @param marshaller
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Element convertToElement(Object obj, String rootName, Marshaller marshaller) throws JAXBException, ParserConfigurationException {
		Document doc = W3CUtils.createDocument();
		marshaller.marshal(new JAXBElement(new QName(rootName), obj.getClass(), obj), doc);
		return doc.getDocumentElement();
	}

}
