//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.27 at 08:51:03 PM CST 
//


package com.siteview.jaxb.svg11;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "descOrTitleOrMetadata"
})
@XmlRootElement(name = "animateTransform")
public class AnimateTransform {

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(name = "xml:base")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlBase;
    @XmlAttribute(name = "xml:lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlLang;
    @XmlAttribute(name = "xml:space")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlSpace;
    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredFeatures;
    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredExtensions;
    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String systemLanguage;
    @XmlAttribute(name = "onbegin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onbegin;
    @XmlAttribute(name = "onend")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onend;
    @XmlAttribute(name = "onrepeat")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onrepeat;
    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onload;
    @XmlAttribute(name = "externalResourcesRequired")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String externalResourcesRequired;
    @XmlAttribute(name = "xmlns:xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlnsXlink;
    @XmlAttribute(name = "xlink:type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkType;
    @XmlAttribute(name = "xlink:href")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkHref;
    @XmlAttribute(name = "xlink:role")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkRole;
    @XmlAttribute(name = "xlink:arcrole")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkArcrole;
    @XmlAttribute(name = "xlink:title")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkTitle;
    @XmlAttribute(name = "xlink:show")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkShow;
    @XmlAttribute(name = "xlink:actuate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkActuate;
    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeName;
    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeType;
    @XmlAttribute(name = "begin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String begin;
    @XmlAttribute(name = "dur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String dur;
    @XmlAttribute(name = "end")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String end;
    @XmlAttribute(name = "min")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String min;
    @XmlAttribute(name = "max")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String max;
    @XmlAttribute(name = "restart")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String restart;
    @XmlAttribute(name = "repeatCount")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String repeatCount;
    @XmlAttribute(name = "repeatDur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String repeatDur;
    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fill;
    @XmlAttribute(name = "calcMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String calcMode;
    @XmlAttribute(name = "values")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String values;
    @XmlAttribute(name = "keyTimes")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String keyTimes;
    @XmlAttribute(name = "keySplines")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String keySplines;
    @XmlAttribute(name = "from")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String from;
    @XmlAttribute(name = "to")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String to;
    @XmlAttribute(name = "by")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String by;
    @XmlAttribute(name = "additive")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String additive;
    @XmlAttribute(name = "accumulate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String accumulate;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlElements({
        @XmlElement(name = "desc", type = Desc.class),
        @XmlElement(name = "title", type = Title.class),
        @XmlElement(name = "metadata", type = Metadata.class)
    })
    protected List<Object> descOrTitleOrMetadata;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the xmlBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlBase() {
        return xmlBase;
    }

    /**
     * Sets the value of the xmlBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlBase(String value) {
        this.xmlBase = value;
    }

    /**
     * Gets the value of the xmlLang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlLang() {
        return xmlLang;
    }

    /**
     * Sets the value of the xmlLang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

    /**
     * Gets the value of the xmlSpace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlSpace() {
        return xmlSpace;
    }

    /**
     * Sets the value of the xmlSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlSpace(String value) {
        this.xmlSpace = value;
    }

    /**
     * Gets the value of the requiredFeatures property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequiredFeatures() {
        return requiredFeatures;
    }

    /**
     * Sets the value of the requiredFeatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequiredFeatures(String value) {
        this.requiredFeatures = value;
    }

    /**
     * Gets the value of the requiredExtensions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequiredExtensions() {
        return requiredExtensions;
    }

    /**
     * Sets the value of the requiredExtensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequiredExtensions(String value) {
        this.requiredExtensions = value;
    }

    /**
     * Gets the value of the systemLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemLanguage() {
        return systemLanguage;
    }

    /**
     * Sets the value of the systemLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    /**
     * Gets the value of the onbegin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnbegin() {
        return onbegin;
    }

    /**
     * Sets the value of the onbegin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnbegin(String value) {
        this.onbegin = value;
    }

    /**
     * Gets the value of the onend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnend() {
        return onend;
    }

    /**
     * Sets the value of the onend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnend(String value) {
        this.onend = value;
    }

    /**
     * Gets the value of the onrepeat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnrepeat() {
        return onrepeat;
    }

    /**
     * Sets the value of the onrepeat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnrepeat(String value) {
        this.onrepeat = value;
    }

    /**
     * Gets the value of the onload property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnload() {
        return onload;
    }

    /**
     * Sets the value of the onload property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnload(String value) {
        this.onload = value;
    }

    /**
     * Gets the value of the externalResourcesRequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    /**
     * Sets the value of the externalResourcesRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalResourcesRequired(String value) {
        this.externalResourcesRequired = value;
    }

    /**
     * Gets the value of the xmlnsXlink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlnsXlink() {
        if (xmlnsXlink == null) {
            return "http://www.w3.org/1999/xlink";
        } else {
            return xmlnsXlink;
        }
    }

    /**
     * Sets the value of the xmlnsXlink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlnsXlink(String value) {
        this.xmlnsXlink = value;
    }

    /**
     * Gets the value of the xlinkType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkType() {
        if (xlinkType == null) {
            return "simple";
        } else {
            return xlinkType;
        }
    }

    /**
     * Sets the value of the xlinkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkType(String value) {
        this.xlinkType = value;
    }

    /**
     * Gets the value of the xlinkHref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkHref() {
        return xlinkHref;
    }

    /**
     * Sets the value of the xlinkHref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkHref(String value) {
        this.xlinkHref = value;
    }

    /**
     * Gets the value of the xlinkRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkRole() {
        return xlinkRole;
    }

    /**
     * Sets the value of the xlinkRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkRole(String value) {
        this.xlinkRole = value;
    }

    /**
     * Gets the value of the xlinkArcrole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkArcrole() {
        return xlinkArcrole;
    }

    /**
     * Sets the value of the xlinkArcrole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkArcrole(String value) {
        this.xlinkArcrole = value;
    }

    /**
     * Gets the value of the xlinkTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkTitle() {
        return xlinkTitle;
    }

    /**
     * Sets the value of the xlinkTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkTitle(String value) {
        this.xlinkTitle = value;
    }

    /**
     * Gets the value of the xlinkShow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkShow() {
        if (xlinkShow == null) {
            return "other";
        } else {
            return xlinkShow;
        }
    }

    /**
     * Sets the value of the xlinkShow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkShow(String value) {
        this.xlinkShow = value;
    }

    /**
     * Gets the value of the xlinkActuate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXlinkActuate() {
        if (xlinkActuate == null) {
            return "onLoad";
        } else {
            return xlinkActuate;
        }
    }

    /**
     * Sets the value of the xlinkActuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXlinkActuate(String value) {
        this.xlinkActuate = value;
    }

    /**
     * Gets the value of the attributeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the value of the attributeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    /**
     * Gets the value of the attributeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * Sets the value of the attributeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    /**
     * Gets the value of the begin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBegin() {
        return begin;
    }

    /**
     * Sets the value of the begin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBegin(String value) {
        this.begin = value;
    }

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDur(String value) {
        this.dur = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * Gets the value of the min property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMin(String value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMax(String value) {
        this.max = value;
    }

    /**
     * Gets the value of the restart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestart() {
        if (restart == null) {
            return "always";
        } else {
            return restart;
        }
    }

    /**
     * Sets the value of the restart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestart(String value) {
        this.restart = value;
    }

    /**
     * Gets the value of the repeatCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepeatCount() {
        return repeatCount;
    }

    /**
     * Sets the value of the repeatCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepeatCount(String value) {
        this.repeatCount = value;
    }

    /**
     * Gets the value of the repeatDur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepeatDur() {
        return repeatDur;
    }

    /**
     * Sets the value of the repeatDur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepeatDur(String value) {
        this.repeatDur = value;
    }

    /**
     * Gets the value of the fill property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFill() {
        if (fill == null) {
            return "remove";
        } else {
            return fill;
        }
    }

    /**
     * Sets the value of the fill property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFill(String value) {
        this.fill = value;
    }

    /**
     * Gets the value of the calcMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalcMode() {
        if (calcMode == null) {
            return "linear";
        } else {
            return calcMode;
        }
    }

    /**
     * Sets the value of the calcMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalcMode(String value) {
        this.calcMode = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValues(String value) {
        this.values = value;
    }

    /**
     * Gets the value of the keyTimes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyTimes() {
        return keyTimes;
    }

    /**
     * Sets the value of the keyTimes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyTimes(String value) {
        this.keyTimes = value;
    }

    /**
     * Gets the value of the keySplines property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeySplines() {
        return keySplines;
    }

    /**
     * Sets the value of the keySplines property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeySplines(String value) {
        this.keySplines = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the by property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBy() {
        return by;
    }

    /**
     * Sets the value of the by property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBy(String value) {
        this.by = value;
    }

    /**
     * Gets the value of the additive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditive() {
        if (additive == null) {
            return "replace";
        } else {
            return additive;
        }
    }

    /**
     * Sets the value of the additive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditive(String value) {
        this.additive = value;
    }

    /**
     * Gets the value of the accumulate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccumulate() {
        if (accumulate == null) {
            return "none";
        } else {
            return accumulate;
        }
    }

    /**
     * Sets the value of the accumulate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccumulate(String value) {
        this.accumulate = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "translate";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the descOrTitleOrMetadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descOrTitleOrMetadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescOrTitleOrMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Desc }
     * {@link Title }
     * {@link Metadata }
     * 
     * 
     */
    public List<Object> getDescOrTitleOrMetadata() {
        if (descOrTitleOrMetadata == null) {
            descOrTitleOrMetadata = new ArrayList<Object>();
        }
        return this.descOrTitleOrMetadata;
    }

}