package com.siteview.jaxb.svg11.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.siteview.jaxb.svg11.Circle;
import com.siteview.jaxb.svg11.Defs;
import com.siteview.jaxb.svg11.G;
import com.siteview.jaxb.svg11.Line;
import com.siteview.jaxb.svg11.Path;
import com.siteview.jaxb.svg11.Rect;
import com.siteview.jaxb.svg11.Svg;
import com.siteview.jaxb.svg11.Use;

public class SvgMain {

  private static final String SVG_FILE_OUT = "./svg-jaxb-out.svg";
  private static final String SVG_FILE_IN = "./svg-jaxb-in.svg";
  private static final String SVG_FILE_IN_OUT = "./svg-jaxb-in-out.svg";

  public static void main(String[] args) throws JAXBException, IOException {   

	List<Object> svgContentList = new ArrayList<Object>();
	List<Object> gContentList = new ArrayList<Object>();
	List<Object> defsContentList = new ArrayList<Object>();
	
    Rect rect1 = new Rect();
    Line line1 = new Line();
    Path path1 = new Path();
    G group1 = new G();
    Circle circle1 = new Circle();
    Defs defs1 = new Defs();
    Use use1 = new Use();
    Svg svg1 = new Svg();
    

//  <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="1000" height="1000">    
    svg1.setWidth("1000");
    svg1.setHeight("1000");
    svg1.setXmlns("http://www.w3.org/2000/svg");
    svg1.setXmlnsXlink("http://www.w3.org/1999/xlink");
    
    rect1.setId("rect1");
    rect1.setX("200");
    rect1.setY("200");
    rect1.setWidth("100");
    rect1.setHeight("200");
    rect1.setStyle("fill:rgb(0,0,255);stroke-width:3;stroke:rgb(0,0,0)");
    
    line1.setX1("100");
    line1.setY1("100");
    
    line1.setX2("100");
    line1.setY2("300");
    
    path1.setD("M0 100 L100 300 L0 400 Z");
    path1.setFill("rgb(255,0,255)");
    path1.setId("path1");
    
    circle1.setId("circle1");
    circle1.setCx("200");
    circle1.setCy("200");
    circle1.setR("50");
    circle1.setFill("");
    circle1.setStroke("black");
    circle1.setStrokeWidth("3");

    group1.setId("group1");
//    gContentList = group1.getSVGDescriptionClassOrSVGAnimationClassOrSVGStructureClass();
    gContentList = group1.getSubContents();
    gContentList.add(line1);
    gContentList.add(path1);
    gContentList.add(rect1);
    
//    defsContentList = defs1.getSVGDescriptionClassOrSVGAnimationClassOrSVGStructureClass();
    defsContentList = defs1.getSubContents();
    defsContentList.add(circle1);
    
//    <use xlink:href="#circle1" x="400" y="100"/>
    use1.setXlinkHref("#circle1");
    use1.setX("400");
    use1.setY("100");
    use1.setStyle("fill:rgb(0,255,0);stroke-width:3;stroke:rgb(255,0,0)");

    // create JAXB context and instantiate marshaller
    JAXBContext context = JAXBContext.newInstance(Svg.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    
//    svgContentList = svg1.getSVGDescriptionClassOrSVGAnimationClassOrSVGStructureClass();
    svgContentList = svg1.getSubContents();
    svgContentList.add(defs1);
    svgContentList.add(rect1);
    svgContentList.add(line1);
    svgContentList.add(group1);
    svgContentList.add(use1);
    

    // Write to System.out
    m.marshal(svg1, System.out);

    // Write to File
    m.marshal(svg1, new File(SVG_FILE_OUT));

    // get variables from our xml file, created before
    Unmarshaller um = context.createUnmarshaller();
    
    Source source = new StreamSource(new FileReader(SVG_FILE_IN));
    JAXBElement<Svg> root = um.unmarshal(source, Svg.class);
    Svg svg2 = root.getValue();
    svg2.setHeight("500");
//    svgContentList = svg2.getSVGDescriptionClassOrSVGAnimationClassOrSVGStructureClass();
    svgContentList = svg2.getSubContents();
    
    svgContentList.add(line1);
    rect1.setId("rect2");
    svgContentList.add(rect1);
    
    for (Object svgContent: svgContentList) {
    	Object element = svgContent;
    	if(svgContent instanceof JAXBElement) element = ((JAXBElement) svgContent).getValue();
    	 
        if( element instanceof com.siteview.jaxb.svg11.Rect ){
        	Rect rect = (Rect) element;
        	if (rect.getId().equalsIgnoreCase("rect2")) {
        		rect.setX("200");
        		rect.setY("100");
        	}
        } else if( element instanceof com.siteview.jaxb.svg11.G ){
        	String gId = ((G) element).getId();            
        } else if( element instanceof com.siteview.jaxb.svg11.Line ){
        	((Line) element).setX1("100");            
        } else if( element instanceof Path ){
            
        } else {
//            throw new IllegalArgumentException( "class " + element.getClass() );
        }
    }

    m.marshal(svg2, new File(SVG_FILE_IN_OUT));
    
    System.out.println();
    System.out.println("Output from our XML File: ");
    // Write to System.out
    m.marshal(svg2, System.out);
        
  }
}