//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.09 at 01:28:45 PM MESZ 
//


package de.sernet.model.licensemanagement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for licenseManagementEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="licenseManagementEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contentIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="licenseID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="salt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validUntil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validUsers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "licenseManagementEntry", propOrder = {
        "e1",
        "e2",
        "e3",
        "e4",
        "e5",
        "e6",
    })
@XmlRootElement
public class LicenseManagementEntry {

    @XmlElement(required = true)
    protected String e1;
    @XmlElement(required = true)
    protected String e2;
    protected String e3;
    protected String e4;
    protected String e5;
    protected String e6;

    /**
     * Gets the value of the e1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE1() {
        return e1;
    }

    /**
     * Sets the value of the e1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE1(String value) {
        this.e1 = value;
    }

    /**
     * Gets the value of the e2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE2() {
        return e2;
    }

    /**
     * Sets the value of the e2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE2(String value) {
        this.e2 = value;
    }

    /**
     * Gets the value of the e3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE3() {
        return e3;
    }

    /**
     * Sets the value of the e3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE3(String value) {
        this.e3 = value;
    }

    /**
     * Gets the value of the e4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE4() {
        return e4;
    }

    /**
     * Sets the value of the e4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE4(String value) {
        this.e4 = value;
    }

    /**
     * Gets the value of the e5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE5() {
        return e5;
    }

    /**
     * Sets the value of the e5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE5(String value) {
        this.e5 = value;
    }

    /**
     * Gets the value of the e6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getE6() {
        return e6;
    }

    /**
     * Sets the value of the e6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setE6(String value) {
        this.e6 = value;
    }

}