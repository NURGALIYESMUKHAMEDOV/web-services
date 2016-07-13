
package kz.gamma.webra.services.client.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for echo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="echo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="testParam" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "echo", propOrder = {
    "testParam"
})
public class Echo {

    protected String testParam;

    /**
     * Gets the value of the testParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestParam() {
        return testParam;
    }

    /**
     * Sets the value of the testParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestParam(String value) {
        this.testParam = value;
    }

}
