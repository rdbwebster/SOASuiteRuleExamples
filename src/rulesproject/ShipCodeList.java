package rulesproject;

import java.util.ArrayList;
import java.util.List;

// A Java Bean used as a Java based fact in the rule engine

public class ShipCodeList {
    public ShipCodeList() {
        super();
        shipList = new ArrayList<ShipCode>();
    }
    
    private List<ShipCode> shipList;

    public void setShipList(List<ShipCode> shipList) {
        this.shipList = shipList;
    }

    public List<ShipCode> getShipList() {
        if(shipList == null)
            shipList = new ArrayList<ShipCode>();
        return shipList;
    }
}
