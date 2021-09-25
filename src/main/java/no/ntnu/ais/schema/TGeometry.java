
package no.ntnu.ais.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TGeometry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TGeometry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="OffsetPosition" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TPosition" minOccurs="0"/>
 *         &lt;element name="OffsetRotation" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TEuler" minOccurs="0"/>
 *         &lt;element name="Shape" type="{http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig}TShape"/>
 *       &lt;/all>
 *       &lt;attribute name="color" type="{http://www.w3.org/2001/XMLSchema}string" default="0x808080" />
 *       &lt;attribute name="opacity" type="{http://www.w3.org/2001/XMLSchema}float" default="1" />
 *       &lt;attribute name="wireframe" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TGeometry", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig", propOrder = {

})
public class TGeometry {

    @XmlElement(name = "OffsetPosition", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig")
    protected TPosition offsetPosition;
    @XmlElement(name = "OffsetRotation", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig")
    protected TEuler offsetRotation;
    @XmlElement(name = "Shape", namespace = "http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig", required = true)
    protected TShape shape;
    @XmlAttribute(name = "color")
    protected String color;
    @XmlAttribute(name = "opacity")
    protected Float opacity;
    @XmlAttribute(name = "wireframe")
    protected Boolean wireframe;

    /**
     * Gets the value of the offsetPosition property.
     * 
     * @return
     *     possible object is
     *     {@link TPosition }
     *     
     */
    public TPosition getOffsetPosition() {
        return offsetPosition;
    }

    /**
     * Sets the value of the offsetPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPosition }
     *     
     */
    public void setOffsetPosition(TPosition value) {
        this.offsetPosition = value;
    }

    /**
     * Gets the value of the offsetRotation property.
     * 
     * @return
     *     possible object is
     *     {@link TEuler }
     *     
     */
    public TEuler getOffsetRotation() {
        return offsetRotation;
    }

    /**
     * Sets the value of the offsetRotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TEuler }
     *     
     */
    public void setOffsetRotation(TEuler value) {
        this.offsetRotation = value;
    }

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link TShape }
     *     
     */
    public TShape getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link TShape }
     *     
     */
    public void setShape(TShape value) {
        this.shape = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColor() {
        if (color == null) {
            return "0x808080";
        } else {
            return color;
        }
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the opacity property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public float getOpacity() {
        if (opacity == null) {
            return  1.0F;
        } else {
            return opacity;
        }
    }

    /**
     * Sets the value of the opacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setOpacity(Float value) {
        this.opacity = value;
    }

    /**
     * Gets the value of the wireframe property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isWireframe() {
        if (wireframe == null) {
            return false;
        } else {
            return wireframe;
        }
    }

    /**
     * Sets the value of the wireframe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWireframe(Boolean value) {
        this.wireframe = value;
    }

}
