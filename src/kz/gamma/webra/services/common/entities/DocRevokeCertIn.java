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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.gamma.kz/webra/xsd}PkiDocument">
 *       &lt;sequence>
 *         &lt;element name="signedCMS" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "signedCMS",
    "serialNumber"
})
@XmlRootElement(name = "docRevokeCertIn")
public class DocRevokeCertIn
    extends PkiDocument
{

    @XmlElement(required = true)
    protected byte[] signedCMS;
    @XmlElement(required = true)
    protected byte[] serialNumber;

    /**
     * Gets the value of the signedCMS property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSignedCMS() {
        return signedCMS;
    }

    /**
     * Sets the value of the signedCMS property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSignedCMS(byte[] value) {
        this.signedCMS = ((byte[]) value);
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSerialNumber(byte[] value) {
        this.serialNumber = ((byte[]) value);
    }

}
