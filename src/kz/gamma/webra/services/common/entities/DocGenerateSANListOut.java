
package kz.gamma.webra.services.common.entities;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="sanList" type="{http://www.gamma.kz/webra/xsd}string255TYPE" maxOccurs="unbounded" minOccurs="0"/>
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
        "sanList"
})
@XmlRootElement(name = "docGenerateSANListOut")
public class DocGenerateSANListOut
        extends PkiDocument
{

    protected List<String> sanList;

    /**
     * Gets the value of the snList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the snList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSnList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSnList() {
        if (sanList == null) {
            sanList = new ArrayList<String>();
        }
        return this.sanList;
    }

}
