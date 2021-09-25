
package no.ntnu.ais.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TTransform complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TTransform">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Geometry" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TGeometry"/>
 *         &lt;element name="Position" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TPosition" minOccurs="0"/>
 *         &lt;element name="Rotation" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TEuler" minOccurs="0"/>
 *         &lt;element name="Trail" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TTrail" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="parent" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TTransform", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig", propOrder = {

})
public class TTransform {

    @XmlElement(name = "Geometry", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig", required = true)
    protected TGeometry geometry;
    @XmlElement(name = "Position", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig")
    protected TPosition position;
    @XmlElement(name = "Rotation", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig")
    protected TEuler rotation;
    @XmlElement(name = "Trail", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig")
    protected TTrail trail;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "parent")
    protected String parent;

    /**
     * Gets the value of the geometry property.
     * 
     * @return
     *     possible object is
     *     {@link TGeometry }
     *     
     */
    public TGeometry getGeometry() {
        return geometry;
    }

    /**
     * Sets the value of the geometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link TGeometry }
     *     
     */
    public void setGeometry(TGeometry value) {
        this.geometry = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link TPosition }
     *     
     */
    public TPosition getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPosition }
     *     
     */
    public void setPosition(TPosition value) {
        this.position = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link TEuler }
     *     
     */
    public TEuler getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TEuler }
     *     
     */
    public void setRotation(TEuler value) {
        this.rotation = value;
    }

    /**
     * Gets the value of the trail property.
     * 
     * @return
     *     possible object is
     *     {@link TTrail }
     *     
     */
    public TTrail getTrail() {
        return trail;
    }

    /**
     * Sets the value of the trail property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTrail }
     *     
     */
    public void setTrail(TTrail value) {
        this.trail = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the parent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParent() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParent(String value) {
        this.parent = value;
    }

}
