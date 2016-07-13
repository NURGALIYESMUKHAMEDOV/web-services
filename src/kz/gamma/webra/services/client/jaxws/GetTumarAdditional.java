
package kz.gamma.webra.services.client.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTumarAdditional complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTumarAdditional">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isManager" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isWithJCE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTumarAdditional", propOrder = {
    "isManager",
    "isWithJCE"
})
public class GetTumarAdditional {

    protected boolean isManager;
    protected boolean isWithJCE;

    /**
     * Gets the value of the isManager property.
     * 
     */
    public boolean isIsManager() {
        return isManager;
    }

    /**
     * Sets the value of the isManager property.
     * 
     */
    public void setIsManager(boolean value) {
        this.isManager = value;
    }

    /**
     * Gets the value of the isWithJCE property.
     * 
     */
    public boolean isIsWithJCE() {
        return isWithJCE;
    }

    /**
     * Sets the value of the isWithJCE property.
     * 
     */
    public void setIsWithJCE(boolean value) {
        this.isWithJCE = value;
    }

}
