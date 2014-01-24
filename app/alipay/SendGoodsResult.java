package alipay;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SendGoodsResult {
	private Document doc;
	private XPath xpath;
	
	private Map<String, String> params = new HashMap<String, String>();
	private String success;
	private String errorMsg;
	
	public SendGoodsResult(String xml){
		try{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));	
			
			XPathFactory xFactory = XPathFactory.newInstance();
			this.xpath = xFactory.newXPath();
			
			this.success = getNodeValue("//*[local-name()='is_success']");
			
			if(isTranSuccessful()){
				//parse parameters.
				XPathExpression expr = xpath.compile("//*[local-name()='tradeBase']");
				Node paramNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
				NodeList children = paramNode.getChildNodes();
				params = new HashMap<String, String>();
				for(int i=0; i< children.getLength(); i++){
					Node child = children.item(i);
					params.put(child.getNodeName(), child.getTextContent());
				}
				String sign = getNodeValue("//*[local-name()='sign']");
				params.put("sign", sign);
			}else{
				//set error message.
				errorMsg = getNodeValue("//*[local-name()='error']");
			}
			
		}catch(Exception e){
			throw new IllegalArgumentException("The xml provided is not well-formated!");
		}
	}

	public boolean isTranSuccessful() {
		return success.equalsIgnoreCase("T");
	}

	public boolean isCompleted() {
		String status = params.get("trade_status");
		return status !=null && status.equals("WAIT_BUYER_CONFIRM_GOODS");
	}
	
	public String getErrorMessage(){
		return errorMsg;
	}
	
	public Map<String, String> getParams(){
		return params;
	}
	
	private String getNodeValue(String xpathStr) {
		try {
			XPathExpression expr = xpath.compile(xpathStr);
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			if(result.getLength()>0){
				String value =  result.item(0).getTextContent();
				return value==null?"":value.trim();
			}
			return null;
		} catch (XPathExpressionException e) {
			return null;
		}
	}

}
