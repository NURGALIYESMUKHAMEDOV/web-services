//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.02 at 02:22:29 PM ALMT 
//


package kz.gamma.webra.services.common.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;


/**
 * OrderDetail
 * 
 * <p>Java class for OrderDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrderDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.gamma.kz/webra/xsd}uLong20TYPE" minOccurs="0"/>
 *         &lt;element name="dn" type="{http://www.gamma.kz/webra/xsd}string255TYPE"/>
 *         &lt;element name="tariffId" type="{http://www.gamma.kz/webra/xsd}uLong20TYPE"/>
 *         &lt;element name="claimCount" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderDetail", propOrder = {
    "id",
    "dn",
    "tariffId",
    "claimCount"
})
public class OrderDetail {

    protected BigInteger id;
    @XmlElement(required = true)
    protected String dn;
    @XmlElement(required = true)
    protected BigInteger tariffId;
   // @XmlElement(required = true)
    protected BigInteger claimCount;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the dn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDn() {
        return dn;
    }

    /**
     * Sets the value of the dn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDn(String value) {
        this.dn = value;
    }

    /**
     * Gets the value of the tariffId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTariffId() {
        return tariffId;
    }

    /**
     * Sets the value of the tariffId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTariffId(BigInteger value) {
        this.tariffId = value;
    }

    /**
     * Gets the value of the claimCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getClaimCount() {
        return claimCount;
    }

    /**
     * Sets the value of the claimCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setClaimCount(BigInteger value) {
        this.claimCount = value;
    }

}
