package rulesproject;

import example.rules.OrderT;
import example.rules.ObjectFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MyShippingCostTester {
  public MyShippingCostTester() {
    super();
  }
    
  /**
   * Creates a list of Shipping Codes Java Facts for input to the rule engine. 
   * @return A populated List of ShipCode objects
   */
  public static List<ShipCode> createShipCodesTestData()
  { 
      ShipCode row1 = new ShipCode();
      row1.setProductId("001");
      row1.setShipCode("C");
      
      ShipCode row2 = new ShipCode();
      row2.setProductId("002");
      row2.setShipCode("A");
      
      List<ShipCode> codes = new ArrayList<ShipCode>();
      codes.add(row1);
      codes.add(row2);
      
      return codes;
  }
  
  /**
   * Creates a sample order for input to the rule engine.  Order is an XML type Fact.
   * @return A populated sample OrderT object
   * @throws JAXBException If unmarshalling is unsuccessful
   */
  public static OrderT createOrderTestData() throws JAXBException {
    
           String   sampleOrder =
                          "<Order xmlns=\"http://www.rules.example\">\n" + 
                          "  <orderId></orderId>\n" + 
                          "  <items>\n" + 
                          "    <item>\n" + 
                          "      <productId>001</productId>\n" + 
                          "      <quantity>3</quantity>\n" + 
                          "      <unitPrice>5.99</unitPrice>\n" + 
                          "      <extraCharge>false</extraCharge>\n" + 
                          "    </item>\n" + 
                          "    <item>\n" + 
                          "      <productId>123</productId>\n" + 
                          "      <quantity>1</quantity>\n" + 
                          "      <unitPrice>34.99</unitPrice>\n" + 
                          "      <extraCharge>false</extraCharge>\n" + 
                          "    </item>\n" + 
                          "  </items>\n" + 
                          "</Order>";
                
           OrderT order = null;
           
           // XML Facts are represented by JAXB types. 
           // Use the Generated types to parse an xml test message
           JAXBContext jaxbContext2 = JAXBContext.newInstance("example.rules");
           Unmarshaller unMarsh = jaxbContext2.createUnmarshaller();
           ByteArrayInputStream is = new ByteArrayInputStream(sampleOrder.getBytes());
           Object obj = unMarsh.unmarshal(is);
           JAXBElement jobj = (JAXBElement) obj;
           order = (OrderT) jobj.getValue();
          
           // Write input Order to stdout for later comparison to Order returned by rule engine 
           System.out.println("Input Order Is:");
           Marshaller marshaller2 = jaxbContext2.createMarshaller();
           marshaller2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
           marshaller2.marshal( obj, System.out );
                     
           return order;      
  }  
  
  public static void main(String args[]) throws Exception {

    final String  dictionaryLocation = "C:\\projects\\BobsRulesApp\\oracle\\rules\\rulesproject\\OracleRules1.rules";
    final String  decisionServiceName = "DetermineShippingCosts";
           
    // Create an array of inputs that match the Rules Decision Function parameters
    ArrayList inputs = new ArrayList();
    inputs.add(createOrderTestData());      // OrderT
    inputs.add(createShipCodesTestData());  // Ship Code List
         
    // Execute the Rules passing in the Test Order and Shipping Codes
    List resultList = RuleTester.runRules(dictionaryLocation, decisionServiceName, inputs, false);     // trace off
    
    // Output the results
    if(resultList != null)
          {
             // Only a single Order object returned from rules decision function
              OrderT result = (OrderT) resultList.get(0);  
       
              // Output Modified Order returned from Rules Engine to stdout 
              JAXBContext jaxbContext = JAXBContext.newInstance(OrderT.class);
              Marshaller marshaller = jaxbContext.createMarshaller();
              marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
              ObjectFactory objF = new ObjectFactory();
              marshaller.marshal( objF.createOrder(result), System.out );
          }
    }
  
}
