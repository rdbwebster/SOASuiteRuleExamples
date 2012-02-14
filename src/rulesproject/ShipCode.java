package rulesproject;

// A Java Bean used as a Java based fact in the rule engine

public class ShipCode {
    public ShipCode() {
        super();
    }
    
    private String productId;
    private String shipCode;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setShipCode(String shipCode) {
        this.shipCode = shipCode;
    }

    public String getShipCode() {
        return shipCode;
    }
}
