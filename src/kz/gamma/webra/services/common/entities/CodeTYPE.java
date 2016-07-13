//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.02 at 02:22:29 PM ALMT 
//


package kz.gamma.webra.services.common.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for codeTYPE.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="codeTYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="xsdValidationError"/>
 *     &lt;enumeration value="signError"/>
 *     &lt;enumeration value="serverError"/>
 *     &lt;enumeration value="uniqueError"/>
 *     &lt;enumeration value="entityNotFound"/>
 *     &lt;enumeration value="payError"/>
 *     &lt;enumeration value="incorrectDataError"/>
 *     &lt;enumeration value="commonError"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "codeTYPE")
@XmlEnum
public enum CodeTYPE {


    /**
     * Ошибка валидации по XSD. При наличии неправильно сформированных xml сообщений.
     * 
     */
    @XmlEnumValue("xsdValidationError")
    XSD_VALIDATION_ERROR("xsdValidationError"),

    /**
     * Ошибка подписи. При ошибках криптографии и подписи.
     * 
     */
    @XmlEnumValue("signError")
    SIGN_ERROR("signError"),

    /**
     * Ошибка сервера. При возникновении не предвиденных ошибок на сервере.
     * 
     */
    @XmlEnumValue("serverError")
    SERVER_ERROR("serverError"),

    /**
     * Ошибка уникальности. Возникает при нарушени уникальности сущности в системе.
     * 
     */
    @XmlEnumValue("uniqueError")
    UNIQUE_ERROR("uniqueError"),

    /**
     * Отсутствие требуемой сущности. При отсутвии сущности в системе.
     * 
     */
    @XmlEnumValue("entityNotFound")
    ENTITY_NOT_FOUND("entityNotFound"),

    /**
     * Ошибка, связанная с оплатой.
     * 
     */
    @XmlEnumValue("payError")
    PAY_ERROR("payError"),

    /**
     * Некорректные данные.
     * 
     */
    @XmlEnumValue("incorrectDataError")
    INCORRECT_DATA_ERROR("incorrectDataError"),

    /**
     * Общие ошибки. Ошибки не подходящие ни под одну категорию.
     * 
     */
    @XmlEnumValue("commonError")
    COMMON_ERROR("commonError");
    private final String value;

    CodeTYPE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CodeTYPE fromValue(String v) {
        for (CodeTYPE c: CodeTYPE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}