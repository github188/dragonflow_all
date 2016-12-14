package com.siteview.svgmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.siteview.svgmap.ShapeItem;
import com.siteview.svgmap.VisioMap;
import com.siteview.svgmap.models.*;

public class VisioImporter {

	private long lPageCount = 0,  lPagesPerRow = 1, lShapeCount = 0,
			lShapesCount = 0, PixPerInch = 0, PixPerInchFirst = 100,
			lPagesCount = 0, StartTick = 0, EndTick = 0;
   float	lBasedX = 0, lBasedY = 0, lOffsetX = 0,
			lOffsetXFinal = 0, lOffsetY = 0, lGroupY = 0, lGroupX = 0,
			lGroupPinY = 0, lGroupPinX = 0;
	private double PageScale = 1;
	private Map<String, String> namespaceMap=null ;
	private Document doc;
	private XPath xpath=null;
	private List<Node> all_pages;
	private Node current_page;
	private List<Node> all_shapes;
	private Node current_shape;
	public int svgwidth = 0, svgheight = 0;// svg 长宽

	private Node root, colorsRoot, stylesRoot, mastersRoot, connectRoot,
			facesRoot;
	private Node page_props;
	private String sPageID = "", sDL = "", sMasterID = "", sPenStyle = "",
			sLineStyle = "", sTextStyle = "", sFillStyle = "",
			sFillPattern = "", sLineStyle2 = "", sTextStyle2 = "",
			sFillStyle2 = "";
	private String crFillColor, crFillColor2, crLineColor, crTextColor;

	private VisioMap visioChart;
	private int startX = 0;
	private int startY = 0;

	private DocumentFactory factory;
	public EquipmentInfo pEquipmentInfo;
	public java.util.ArrayList pEquipmentPort = new java.util.ArrayList();
	public java.util.ArrayList pEquipmentContainer = new java.util.ArrayList();
	public java.util.ArrayList pFlowChartBox = new java.util.ArrayList();
	public java.util.ArrayList pEquipmentLamp = new java.util.ArrayList();
	private String svgid = "";// p 端口 pl 端口灯
	public java.util.ArrayList items=new java.util.ArrayList();

	public VisioImporter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param vdxPathName
	 *            Path to the VDX file
	 * @param visioChart
	 *            VisioMap instance
	 * @param PageIndex
	 * @param x
	 *            start x
	 * @param y
	 *            start y
	 */
	public final void ImportPage(String vdxPathName, VisioMap visioChart1,
			int PageIndex, int x, int y) {
		String sResult = "", sTemp = "", sPath = "";// spath xml expression
		try {
			visioChart = visioChart1;
			startX = x;
			startY = y;
			doc = null;
			all_pages = null;
			root = null; // root节点
			current_page = null;// 当前页
			all_shapes = null;
			current_shape = null;
			lPagesCount = 0;
			lShapesCount = 0;
			lPageCount = 0;
			lShapeCount = 0;
			StartTick = 0; // 开始时刻
			EndTick = 0; // 结束时刻
			lOffsetX = 0; // 移动X坐标
			lOffsetY = 0; // 移动Y坐标
			factory = DocumentFactory.getInstance();  
	        SAXReader reader = new SAXReader(factory);       
	        // 解析具有命名空间的XML文档  
	        doc = reader.read(new File(vdxPathName));  
	          
	        // 命名空间前缀与URI的映射  
	        namespaceMap = new HashMap<String, String>();  
	        namespaceMap.put("vdx", "http://schemas.microsoft.com/visio/2003/core");  
	         
	        
	       
			// 获取root节点
			root = doc.getRootElement();
			// 页的查询path
			if (PageIndex == -1) {
				sPath = "//vdx:Pages/vdx:Page";
			} else {
				sPath = String.format("//vdx:Pages/vdx:Page[%1$s]",
						PageIndex + 1);
			}
			xpath = factory.createXPath(sPath);  
	        xpath.setNamespaceURIs(namespaceMap); 
			all_pages = xpath.selectNodes(root);
			lPageCount = all_pages.size();
			// Hash some common-used VDX nodes

			colorsRoot = null;
			xpath = factory.createXPath("//vdx:Colors");  
	        xpath.setNamespaceURIs(namespaceMap); 
			colorsRoot =xpath.selectSingleNode(doc); 
					

			mastersRoot = null;
			xpath = factory.createXPath("//vdx:Masters");  
	        xpath.setNamespaceURIs(namespaceMap); 
			mastersRoot = xpath.selectSingleNode(root);


			stylesRoot = null;
			xpath = factory.createXPath("//vdx:StyleSheets");  
	        xpath.setNamespaceURIs(namespaceMap); 
	        stylesRoot =xpath.selectSingleNode(root);


			facesRoot = null;
			xpath = factory.createXPath("//vdx:FaceNames");  
	        xpath.setNamespaceURIs(namespaceMap); 
	        facesRoot=xpath.selectSingleNode(root);
	

			// Setting start tick
			StartTick = System.currentTimeMillis();

			// Page processing loop for all pages required
			for (Node current_page1: all_pages) {
				// Getting page's parameters & properties

				current_page = current_page1;
				if (current_page == null) {
					continue;
				}
				connectRoot = null;
				xpath = factory.createXPath("vdx:Connects");  
		        xpath.setNamespaceURIs(namespaceMap); 
		        connectRoot=xpath.selectSingleNode(current_page);

				lPagesCount++;
				sPageID = ((Element)current_page).attributeValue("ID");
				xpath = factory.createXPath("vdx:PageSheet/vdx:PageProps");  
		        xpath.setNamespaceURIs(namespaceMap); 
				page_props =xpath.selectSingleNode(current_page);
						
				if (page_props == null) {
					continue;
				}

				// Getting default page's measure Unit
				sPath = String
						.format("//vdx:Pages/vdx:Page[@ID='%1$s']/vdx:PageSheet/vdx:PageProps/vdx:PageHeight",
								sPageID);
				sDL = GetShapeAttr(sPath, "Unit");
				if (sDL == null || sDL.trim().equals("")) {
					sDL = "MM";
				}
				// Getting page's scale
				sPath = String
						.format("//vdx:Pages/vdx:Page[@ID='%1$s']/vdx:PageSheet/vdx:PageProps/vdx:PageScale",
								sPageID);
				sTemp = GetShapeAttr(sPath, "");

				// sTemp = sTemp.replace(".", sSeparator);
				PageScale = Double.parseDouble(sTemp);

				PixPerInch = (long) (PixPerInchFirst / PageScale);

				// Getting page's dimensions
				lBasedX = Measure2Pix(page_props, "PageWidth");
				lBasedY = Measure2Pix(page_props, "PageHeight");

				// Preparing Xpath expression for non-arrow shape loop

				
				xpath = factory.createXPath("vdx:Shapes/vdx:Shape");  
		        xpath.setNamespaceURIs(namespaceMap); 
		        all_shapes =xpath.selectNodes(current_page);
//						
				lShapeCount = all_shapes.size();

				// Shape processing loop for the page selected
				for (Node current_shape2 :  all_shapes) {
					current_shape = current_shape2;
					if (current_shape == null) {
						continue;
					}
					xpath = factory.createXPath("vdx:XForm1D");  
			        xpath.setNamespaceURIs(namespaceMap); 
					Node is_arrow = xpath.selectSingleNode(current_shape);
					if (is_arrow == null) {
						GetShapeRef(current_shape);
					} else {
						continue;
					}
				}

				// Preparing Xpath expression for arrow - like shape loop
				xpath = factory.createXPath("vdx:Shapes/vdx:Shape");  
		        xpath.setNamespaceURIs(namespaceMap); 
		        all_shapes =xpath.selectNodes(current_page);
//						
				lShapeCount = all_shapes.size();

				// Shape processing loop for the page selected
				for (Node current_shape2 :  all_shapes) {
					current_shape = current_shape2;
					if (current_shape == null) {
						continue;
					}

					xpath = factory.createXPath("vdx:XForm1D");  
			        xpath.setNamespaceURIs(namespaceMap); 
					Node is_arrow = xpath.selectSingleNode(current_shape);
					if (is_arrow != null) {
						GetShapeRef(current_shape);
					} else {
						continue;
					}
				}

				lOffsetX += lBasedX;
				if (lPagesCount % lPagesPerRow == 0) {
					lOffsetY += lBasedY;
					lOffsetXFinal = lOffsetX;
					lOffsetX = 0;
				}

			}
			svgwidth = (int) lOffsetXFinal;
			svgheight = (int) lOffsetY;
			// Setting page's size got as FlowChartX Document's size
			if ((lOffsetY != 0) && (lOffsetXFinal != 0)) {

				// pChart.setDocExtents(new System.Drawing.RectangleF(0, 0,
				// lOffsetXFinal, lOffsetY));

			}

			if (lShapesCount == 0) {
				lShapesCount = 1;
			}

			// Setting end tick
			EndTick = System.currentTimeMillis();

			sResult = String
					.format("%1$s page(s) imported\n-----------------------\nOverall time:\t%2$s ms\nShapes & Arrows:\t%3$s\nTime per shape:\t%4$s ms",
							lPagesCount, EndTick - StartTick, lShapesCount,
							(EndTick - StartTick) / lShapesCount);
		    System.out.println(sResult);
			doc = null;

		} catch (Exception ex) {
           System.out.println("ImportPage"+ex.getMessage());
		}
		
	}

	private boolean GetShapeRef(Node shape, String ShapeType) {
		return GetShapeRef(shape, ShapeType, false);
	}

	private boolean GetShapeRef(Node shape) {
		return GetShapeRef(shape, null, false);
	}

	/**
	 * 
	 * @param shape
	 *            node instance
	 * @param ShapeType
	 * @param group
	 *            是否
	 * @return
	 */
	private boolean GetShapeRef(Node shape, String ShapeType, boolean group) {
		try {
			String shapeID = ((Element) shape).attributeValue("ID");
			List<Node> grshape_list = null;
			xpath = factory.createXPath("vdx:Geom");  
	        xpath.setNamespaceURIs(namespaceMap); 
			grshape_list = xpath.selectNodes(shape);
					
			int size = grshape_list.size();
			if (size > 1) {
				for (Node grshape: grshape_list) {

			
					String sTemp = ((Element)grshape).attributeValue("IX");

					if (TestNoFillRef(shape, ShapeType, sTemp)) {
						// 没有闭合点，就是line
						GetLineRef(shape, ShapeType, sTemp, group);
					} else {
						// 有闭合点，是几何图形
						GetGoemRef(shape, ShapeType, sTemp, group);
					}
				}
				return true;
			} else {
				if (TestNoFillRef(shape, ShapeType, "")) {
					// 没有闭合点，就是line
					return GetLineRef(shape, ShapeType, "", group);
				} else {
					// 有闭合点，是几何图形
					return GetGoemRef(shape, ShapeType, "", group);
				}
			}
		} catch (Exception ex) {
			int t = 0;
		}

		return false;

	}

	private boolean GetLineRef(Node shape, String ShapeType, String IX,
			boolean group) {
		if (ShapeType == null) {
			ShapeType = "";
		}
		try {
			String GeomIx = "vdx:Geom";
			if (!IX.equals("")) {
				GeomIx = String.format("vdx:Geom[@IX='%1$s']", IX);
			}

			String sShapeName, sUID = "", sType = "", sNameU = "", sPath = "", sTemp = "";

			long lShapeType = 0, lLineWith = 0;
			float lX = 0, lY = 0, lWidth = 0, lHeight = 0, lPinY = 0, lPinX = 0, lBoxX = 0, lBoxY = 0;
			// 取代 lShapeType = 0,值有 SHAPE 、GROUP、CONNECTOR、ENTITY
			// 、PART_OF_ENTITY、OVAL_PROCESS、RECTANGLE、STORED_DATA、PART_OF_ARROW、HIDDEN、NO_NAMEU、PART_OF_ENTITY2、ENTITY2
			ArrayList lShapeTypes = new ArrayList();
			Node xform1d_node = null;
			Element element = (Element) shape;

			// Incrementing shape's count
			lShapesCount++;
			sUID = element.attributeValue("ID");
			sType = element.attributeValue("Type");
			sType = sType.toLowerCase();

			// Getting 'XForm' node root
			xpath = factory.createXPath("vdx:XForm");  
	        xpath.setNamespaceURIs(namespaceMap); 
			Node xform_node  =xpath.selectSingleNode(shape);

			if (xform_node == null) {
				return false;
			}
			// Getting Master shape's ID if any exist
			sMasterID = "";
			if (element.attributeValue("Master") == null
					|| element.attributeValue("Master").equals("")) {
				if (element.attributeValue("MasterShape") != null
						&& !element.attributeValue("MasterShape").equals("")) {
					sMasterID = element.attributeValue("MasterShape");
				}
			} else {
				sMasterID = element.attributeValue("Master");
			}

			// Getting shape's 'NameU' tag if any exist
			sNameU = "";
			if (element.attributeValue("NameU") != null
					&& !element.attributeValue("NameU").equals("")) {
				sNameU = element.attributeValue("NameU");
			}
			sShapeName = sNameU;
			sNameU = sNameU.trim();
			sNameU = sNameU.toLowerCase();
			if (sNameU.equals("")) {
				lShapeTypes.add("NO_NAMEU");
			}

			if ((sNameU.equals("")) && (!sMasterID.equals(""))) {
				sPath = String.format("vdx:Master[@ID='%1$s']", sMasterID);
				sNameU = GetShapeAttr(sPath, "NameU", mastersRoot);
				sShapeName = sNameU;
			}

			if (sNameU == null) {
				sNameU = "";
			}

			sNameU = sNameU.trim();
			sNameU = sNameU.toLowerCase();
			// Shape is GROUP
			if (sType.indexOf("group") >= 0) {
				lShapeTypes.add("GROUP");
			}
			// Shape is 'arrow - like'
			if ((sNameU.indexOf("connector") >= 0)
					|| (sNameU.indexOf("link") >= 0)
					|| (sNameU.indexOf("generalization") >= 0)
					|| (sNameU.indexOf("binary association") >= 0)
					|| (sNameU.indexOf("composition") >= 0)
					|| (sNameU.equals("relationship"))
					|| (sNameU.indexOf("parent to category") >= 0)
					|| (sNameU.indexOf("categorytochild ") >= 0)) {
				lShapeTypes.add("CONNECTOR");

			} else {
				xpath = factory.createXPath("vdx:XForm1D");  
		        xpath.setNamespaceURIs(namespaceMap);
				xform1d_node = xpath.selectSingleNode(shape);
				if (xform1d_node != null) {
					// return true;
					// ??? REMOVED ??? lShapeType |= (long)
					// ShapeTypeEnum.ST_CONNECTOR;
				}
			}

			// Shape is 'entity - like'

			if ((sNameU.equals("entity 1")) || (sNameU.indexOf("entity.") >= 0)
					|| (sNameU.equals("entity 2")) || (sNameU.equals("entity"))
					|| (sNameU.equals("view"))) {
				lShapeTypes.add("ENTITY");
			}

			// Shape is 'entity2 - like'
			if (sNameU.equals("entity 2")) {
				lShapeTypes.add("ENTITY2");
			}

			// Shape is part of 'entity - like' group
			if (ShapeType.equals("entity")) {
				lShapeTypes.add("PART_OF_ENTITY");
				lShapeTypes.add("SHAPE");
			}

			// Shape is part of 'entity2 - like' group
			if (ShapeType.equals("entity 2")) {
				lShapeTypes.add("PART_OF_ENTITY");
				lShapeTypes.add("PART_OF_ENTITY2");
				lShapeTypes.add("SHAPE");
			}

			// Shape is part of 'arrow - like' group
			if (ShapeType.equals("arrow")) {
				lShapeTypes.add("PART_OF_ARROW");
				lShapeTypes.add("SHAPE");
			}

			if (lShapeTypes.contains("PART_OF_ENTITY")) {
				lShapeTypes.add("OVAL_PROCESS");
				lShapeTypes.add("SHAPE");
			}

			if (sNameU.equals("parameters")) {
				lShapeTypes.add("HIDDEN");
				lShapeTypes.add("SHAPE");
			}

			if (lShapeTypes.size() == 0
					|| (lShapeTypes.size() == 1 && lShapeTypes
							.contains("NO_NAMEU"))) {
				lShapeTypes.add("SHAPE");
			}
			// Getting shape's dimensions and converting it to 'Pixels'
			lWidth = Measure2Pix(xform_node, "Width");
			lHeight = Measure2Pix(xform_node, "Height");
			lX = Measure2Pix(xform_node, "PinX");
			lY = Measure2Pix(xform_node, "PinY");
			lPinX = Measure2Pix(xform_node, "LocPinX");
			lPinY = Measure2Pix(xform_node, "LocPinY");
			double fAngle2 = 0;

			String sAngle = "0", sAngleUnit = "";
			sAngle = GetShapeAttr("vdx:Angle", "", xform_node);
			sAngleUnit = GetShapeAttr("vdx:Angle", "Unit", xform_node);

			fAngle2 = Angle2Deg(sAngleUnit, sAngle);
			if (lPinY == 0) {
				lPinY = lHeight;
			} else {
				if (lPinY == lHeight) {
					lPinY = 0;
				}
			}
			if (sNameU.equals("separator")) {
				return false;
			}

			if (lShapeTypes.contains("SHAPE") || lShapeTypes.contains("GROUP")
					|| lShapeTypes.contains("PART_OF_ARROW")) {
				if (group) {
					// Getting coordinates of the upper left corner of the group
					lBoxX = lOffsetX + lGroupX - lGroupPinX;
					lBoxY = lOffsetY + lBasedY - (lGroupY + lGroupPinY);

					// Getting coordinates of the grouped shape
					if (lShapeTypes.contains("ENTITY")) {
						lPinY = 0;
					}

					lBoxX = lBoxX + lX - lPinX;

					// Aplying coordinates adustment is shape is part of
					// 'entity-like' group
					if ((!lShapeTypes.contains("ENTITY"))
							&& (lShapeTypes.contains("PART_OF_ENTITY"))
							&& (lShapeTypes.contains("NO_NAMEU"))) {

						if (lShapeTypes.contains("PART_OF_ENTITY2")) {
							lBoxY = lBoxY + lGroupPinY * 2 - lY;
						} else {
							lBoxY = lBoxY;
						}

					} else {
						lBoxY = lBoxY + lGroupPinY * 2 - lY - lPinY;
					}
				} else {
					lBoxX = lOffsetX + lX - lPinX;
					lBoxY = lOffsetY + lBasedY - (lY + lPinY);
				}
			}
			String[] resultStyle;
			resultStyle = GetStyle(shape, "LineStyle", sLineStyle2);
			sLineStyle = resultStyle[0];
			sLineStyle2 = resultStyle[1];
			// Getting colors
			crLineColor = "";
			crLineColor = GetColor(shape, sLineStyle, "vdx:Line/vdx:LineColor",
					null, null, sLineStyle2);
			sTemp = GetShapeAttr("vdx:Line/vdx:LineWeight", "", shape);
			if (sTemp == null || sTemp.equals("")) {
				sTemp = GetShapeAttr(String.format(
						"vdx:StyleSheet[@ID='%1$s']/vdx:Line/vdx:LineWeight",
						sLineStyle), "", stylesRoot);

			}

			// Getting line width
			if (sTemp != null && !sTemp.equals("")) {
				lLineWith = (long) this.Unit2Pix(sDL, sDL, sTemp);
			} else {
				lLineWith = 0;
			}
			// 平移参数
			String shapeType = "";
			float translatex = 0, translatey = 0;
			translatex = startX + lBoxX;
			translatey = startY + lBoxY;
			List<Node> NodeTo_lists = null;
			
			xpath = factory.createXPath(GeomIx + "/vdx:*");  
	        xpath.setNamespaceURIs(namespaceMap);
			NodeTo_lists = xpath.selectNodes(shape);
					
			vsGeometry[] geometrys;
			boolean Ellipse = false;
			if (NodeTo_lists != null) {
				if (!TestShapeGeomName(NodeTo_lists)) {
					// 处理多Goem情况 非Ellipse与InfiniteLine
					geometrys = new vsGeometry[NodeTo_lists.size()];
					int nums = GetGeometryFromNodeList(NodeTo_lists, geometrys);
					DrawGoem.PageScale = PageScale;
					DrawGoem.Angle = fAngle2;
					DrawGoem.PixPerInch = this.PixPerInch;
					DrawGoem.sDL = this.sDL;
					DrawGoem.GoemWidth = lWidth;
					DrawGoem.GoemHeight = lHeight;
					DrawGoem.svgchart = visioChart;
					DrawGoem.x1 = translatex;
					DrawGoem.y1 = translatey;
					DrawGoem.lLineWith = 1;
					DrawGoem.crLineColor = crLineColor;
					DrawGoem.crFillColor = "none";
					String vspath = DrawGoem.DrawLineTo(geometrys);

				} else {
					Ellipse = true;
					// 处理 Ellipse与InfiniteLine 这两种情况，会只有一行数据没有MoveTo
				}
			}
			if (Ellipse) {
				ShapeItem tempitem = null;
				tempitem = new ShapeItem(visioChart);
				tempitem.setFillcolor("none");
				tempitem.setLinecolor(crLineColor);
				tempitem.setLinewidth(1);
				tempitem.setItemid("g1");
				tempitem.setSvgtype(3);
				tempitem.setEllipse(translatex + lWidth / 2 + "", translatey
						+ lHeight / 2 + "", lWidth / 2 + "", lHeight / 2 + "");
			} else {
				ShapeItem tempitem = null;
				tempitem = new ShapeItem(visioChart);
				tempitem.setFillcolor("none");
				tempitem.setLinecolor(crLineColor);
				tempitem.setLinewidth(1);
				tempitem.setItemid("g1");
				tempitem.setSvgtype(4);
				tempitem.setRect(translatex + "", translatey + "", lWidth + "",
						lHeight + "");
			}

		} catch (Exception ex) {

			System.out.println("GetLineRef"+ex.getMessage());
		}

		return true;
	}

	/**
	 * 获取几何图形
	 * 
	 * @param shape
	 * @param ShapeType
	 * @param IX
	 * @param group
	 * @return
	 */
	private boolean GetGoemRef(Node shape, String ShapeType, String IX,
			boolean group) {

		if (ShapeType == null) {
			ShapeType = "";
		}
		try {
			String GeomIx = "vdx:Geom";
			if (!IX.equals("")) {
				GeomIx = String.format("vdx:Geom[@IX='%1$s']", IX);
			}

			String sShapeName, sUID = "", sType = "", sNameU = "", sPath = "", sTemp = "", sStepX = "", sStepY = "";

			long    lTemp = 0, lAdjustX = 0,lLineWith = 0, lCount = 1,lAdjustY = 0;
			float lX = 0, lY = 0, lWidth = 0, lHeight = 0, lPinY = 0, lPinX = 0, lBoxX = 0, lBoxY = 0,lEndX = 0, lEndY = 0, lBeginX = 0, lBeginY = 0,  lPtX = 0, lPtY = 0, lStepX = 0, lStepY = 0, lStartX = 0, lStartY = 0;
			// 取代 lShapeType = 0,值有 SHAPE 、GROUP、CONNECTOR、ENTITY
			// 、PART_OF_ENTITY、OVAL_PROCESS、RECTANGLE、STORED_DATA、PART_OF_ARROW、HIDDEN、NO_NAMEU、PART_OF_ENTITY2、ENTITY2
			ArrayList lShapeTypes = new ArrayList();
			Node node_temp = null;
			Node xform1d_node = null;
			Element element = (Element) shape;
			double fAngle2 = 0;

			// Incrementing shape's count
			lShapesCount++;
			sUID = element.attributeValue("ID");
			sType = element.attributeValue("Type");
			sType = sType.toLowerCase();

			// Getting 'XForm' node root
			xpath = factory.createXPath("vdx:XForm");  
	        xpath.setNamespaceURIs(namespaceMap); 
			Node xform_node  =xpath.selectSingleNode(shape);


			if (xform_node == null) {
				return false;
			}

			// Getting Master shape's ID if any exist
			sMasterID = "";
			if (element.attributeValue("Master") == null
					|| element.attributeValue("Master").equals("")) {
				if (element.attributeValue("MasterShape") != null
						&& !element.attributeValue("MasterShape").equals("")) {
					sMasterID = element.attributeValue("MasterShape");
				}
			} else {
				sMasterID = element.attributeValue("Master");
			}

			// Getting shape's 'NameU' tag if any exist
			sNameU = "";
			if (element.attributeValue("NameU") != null
					&& !element.attributeValue("NameU").equals("")) {
				sNameU = element.attributeValue("NameU");
			}
			sShapeName = sNameU;
			sNameU = sNameU.trim();
			sNameU = sNameU.toLowerCase();
			if (sNameU.equals("")) {
				lShapeTypes.add("NO_NAMEU");
			}

			if ((sNameU.equals("")) && (!sMasterID.equals(""))) {
				sPath = String.format("vdx:Master[@ID='%1$s']", sMasterID);
				sNameU = GetShapeAttr(sPath, "NameU", mastersRoot);
				sShapeName = sNameU;
			}

			if (sNameU == null) {
				sNameU = "";
			}

			sNameU = sNameU.trim();
			sNameU = sNameU.toLowerCase();

			// Shape is GROUP
			if (sType.indexOf("group") >= 0) {
				lShapeTypes.add("GROUP");
			}
			// Shape is 'arrow - like'
			if ((sNameU.indexOf("connector") >= 0)
					|| (sNameU.indexOf("link") >= 0)
					|| (sNameU.indexOf("generalization") >= 0)
					|| (sNameU.indexOf("binary association") >= 0)
					|| (sNameU.indexOf("composition") >= 0)
					|| (sNameU.equals("relationship"))
					|| (sNameU.indexOf("parent to category") >= 0)
					|| (sNameU.indexOf("categorytochild ") >= 0)) {
				lShapeTypes.add("CONNECTOR");

			} else {
				xpath = factory.createXPath("vdx:XForm1D");  
		        xpath.setNamespaceURIs(namespaceMap);
				xform1d_node = xpath.selectSingleNode(shape);
				if (xform1d_node != null) {
					// return true;
					// ??? REMOVED ??? lShapeType |= (long)
					// ShapeTypeEnum.ST_CONNECTOR;
				}
			}

			// Shape is 'entity - like'

			if ((sNameU.equals("entity 1")) || (sNameU.indexOf("entity.") >= 0)
					|| (sNameU.equals("entity 2")) || (sNameU.equals("entity"))
					|| (sNameU.equals("view"))) {
				lShapeTypes.add("ENTITY");
			}

			// Shape is 'entity2 - like'
			if (sNameU.equals("entity 2")) {
				lShapeTypes.add("ENTITY2");
			}

			// Shape is part of 'entity - like' group
			if (ShapeType.equals("entity")) {
				lShapeTypes.add("PART_OF_ENTITY");
				lShapeTypes.add("SHAPE");
			}

			// Shape is part of 'entity2 - like' group
			if (ShapeType.equals("entity 2")) {
				lShapeTypes.add("PART_OF_ENTITY");
				lShapeTypes.add("PART_OF_ENTITY2");
				lShapeTypes.add("SHAPE");
			}

			// Shape is part of 'arrow - like' group
			if (ShapeType.equals("arrow")) {
				lShapeTypes.add("PART_OF_ARROW");
				lShapeTypes.add("SHAPE");
			}

			if (lShapeTypes.contains("PART_OF_ENTITY")) {
				lShapeTypes.add("OVAL_PROCESS");
				lShapeTypes.add("SHAPE");
			}

			if (sNameU.equals("parameters")) {
				lShapeTypes.add("HIDDEN");
				lShapeTypes.add("SHAPE");
			}

			if (lShapeTypes.size() == 0
					|| (lShapeTypes.size() == 1 && lShapeTypes
							.contains("NO_NAMEU"))) {
				lShapeTypes.add("SHAPE");
			}

			// Getting shape's dimensions and converting it to 'Pixels'
			lWidth = Measure2Pix(xform_node, "Width");
			lHeight = Measure2Pix(xform_node, "Height");
			lX = Measure2Pix(xform_node, "PinX");
			lY = Measure2Pix(xform_node, "PinY");
			lPinX = Measure2Pix(xform_node, "LocPinX");
			lPinY = Measure2Pix(xform_node, "LocPinY");

			if (lPinY == 0) {
				lPinY = lHeight;
			} else {
				if (lPinY == lHeight) {
					lPinY = 0;
				}
			}

			if (((lWidth == 0) || (lHeight == 0))
					&& (!lShapeTypes.contains("CONNECTOR"))) {
				return false;
			}

			if (sNameU.equals("separator")) {
				return false;
			}

			// Getting shape's styles IDs
			String[] resultStyle;
			resultStyle = GetStyle(shape, "LineStyle", sLineStyle2);
			sLineStyle = resultStyle[0];
			sLineStyle2 = resultStyle[1];

			resultStyle = GetStyle(shape, "TextStyle", sTextStyle2);
			sTextStyle = resultStyle[0];
			sTextStyle2 = resultStyle[1];

			resultStyle = GetStyle(shape, "FillStyle", sFillStyle2);
			sFillStyle = resultStyle[0];
			sFillStyle2 = resultStyle[1];

			// Getting colors
			crLineColor = "";
			crLineColor = GetColor(shape, sLineStyle, "vdx:Line/vdx:LineColor",
					null, null, sLineStyle2);

			crFillColor = "";
			crFillColor = GetColor(shape, sFillStyle,
					"vdx:Fill/vdx:FillForegnd",
					"vdx:Fill/vdx:FillForegndTrans", null, sFillStyle2);

			crFillColor2 = "";
			crFillColor2 = GetColor(shape, sFillStyle,
					"vdx:Fill/vdx:FillBkgnd", null, null, sFillStyle2);

			crTextColor = "";
			crTextColor = GetColor(shape, sTextStyle, "vdx:Char/vdx:Color",
					null, null, sTextStyle2);

			// Getting fill patterns
			sFillPattern = "";
			sFillPattern = GetShapeAttr("vdx:Fill/vdx:FillPattern", "", shape);
			if (sFillPattern == null || sFillPattern.equals("")) {
				sFillPattern = GetShapeAttr(String.format(
						"vdx:StyleSheet[@ID='%1$s']/vdx:Fill/vdx:FillPattern",
						sFillStyle), "", stylesRoot);

			}

			sTemp = GetShapeAttr("vdx:Line/vdx:LineWeight", "", shape);
			if (sTemp == null || sTemp.equals("")) {
				sTemp = GetShapeAttr(String.format(
						"vdx:StyleSheet[@ID='%1$s']/vdx:Line/vdx:LineWeight",
						sLineStyle), "", stylesRoot);

			}

			// Getting line width
			if (sTemp != null && !sTemp.equals("")) {
				lLineWith = (long) this.Unit2Pix(sDL, sDL, sTemp);
			} else {
				lLineWith = 0;
			}

			// Getting rotaton angle
			String sAngle = "0", sAngleUnit = "";
			sAngle = GetShapeAttr("vdx:Angle", "", xform_node);
			sAngleUnit = GetShapeAttr("vdx:Angle", "Unit", xform_node);

			fAngle2 = Angle2Deg(sAngleUnit, sAngle);

			String fFlipX = GetShapeAttr("vdx:FlipX", "", xform_node);
			String fFlipY = GetShapeAttr("vdx:FlipY", "", xform_node);
			sPenStyle = "";
			sPenStyle = GetShapeAttr("vdx:Line/vdx:LinePattern", "", shape);
			if (sPenStyle == null) {
				sPenStyle = GetShapeAttr(String.format(
						"vdx:StyleSheet[@ID='%1$s']/vdx:Line/vdx:LinePattern",
						sLineStyle), "", stylesRoot);
			}
			if (lShapeTypes.contains("SHAPE") || lShapeTypes.contains("GROUP")
					|| lShapeTypes.contains("PART_OF_ARROW")) {
				if (group) {
					// Getting coordinates of the upper left corner of the group
					lBoxX = lOffsetX + lGroupX - lGroupPinX;
					lBoxY = lOffsetY + lBasedY - (lGroupY + lGroupPinY);

					// Getting coordinates of the grouped shape
					if (lShapeTypes.contains("ENTITY")) {
						lPinY = 0;
					}

					lBoxX = lBoxX + lX - lPinX;

					// Aplying coordinates adustment is shape is part of
					// 'entity-like' group
					if ((!lShapeTypes.contains("ENTITY"))
							&& (lShapeTypes.contains("PART_OF_ENTITY"))
							&& (lShapeTypes.contains("NO_NAMEU"))) {

						if (lShapeTypes.contains("PART_OF_ENTITY2")) {
							lBoxY = lBoxY + lGroupPinY * 2 - lY;
						} else {
							lBoxY = lBoxY;
						}

					} else {
						lBoxY = lBoxY + lGroupPinY * 2 - lY - lPinY;
					}
				} else {
					lBoxX = lOffsetX + lX - lPinX;
					lBoxY = lOffsetY + lBasedY - (lY + lPinY);
				}
				// 平移参数
				String shapeType = "";
				float translatex = 0, translatey = 0;
				translatex = startX + lBoxX;
				translatey = startY + lBoxY;
				if (sNameU.equals("")) {
					sTemp = sType;
				} else {
					sTemp = sNameU;
				}
				String NoFill = GetShapeAttr(GeomIx + "/vdx:NoFill", "", shape);
				xpath = factory.createXPath(GeomIx+"/vdx:Ellipse");  
		        xpath.setNamespaceURIs(namespaceMap);
				Node Ellipse_node =xpath.selectSingleNode(shape);
				
				if (Ellipse_node != null) {
					sTemp = "circle";
				}
				if (lShapeTypes.contains("PART_OF_ENTITY")) {
					shapeType = VisioShape2PredefinedShape("Process");
				} else {
					shapeType = VisioShape2PredefinedShape(sTemp);
				}
				if (shapeType == null) {
					return false;
				}
				// crFillColor crLineColor
				if (NoFill != null && NoFill.equals("1")) {
					crFillColor = "none";
				}
				
				//0无 （透明填充）1稳定的前景颜色。2-40各种类型的填充图案，与"填充"对话框中的索引项相对应。
				if(sFillPattern!=null && sFillPattern.equals("0"))
				{
					crFillColor = "none";
				}
				if(NoFill !=null && NoFill.equals("0"))
				{
					if (crFillColor==null || crFillColor.equals(""))
					{
						//为背景色
						crFillColor="#FFFFFF";
					}
				}
				String NoLine = GetShapeAttr(GeomIx + "/vdx:NoLine", "", shape);
				if (NoLine != null && NoLine.equals("1")) {
					crLineColor = "none";
				}
				
				if(sPenStyle != null && sPenStyle.equals("0"))
				{
					crLineColor = "none";
				}
				if (NoLine != null && NoLine.equals("0")) {
					if (crLineColor==null || crLineColor.equals(""))
					{
						//为背景色
						crLineColor="#000000";
					}
					
				}
				//
				int customtype = customShape(shape);
				this.svgid = "";
				String PhysicsPortIndex="";
				if (customtype == 1) {
					EquipmentPort equipmentPort = (EquipmentPort) this.pEquipmentPort
							.get(this.pEquipmentPort.size() - 1);
					this.svgid = "p" + equipmentPort.PhysicsPortIndex;
					PhysicsPortIndex=equipmentPort.PhysicsPortIndex+"";
				

				} else if (customtype == 2) {
					EquipmentLamp equipmentLamp = (EquipmentLamp) this.pEquipmentLamp
							.get(this.pEquipmentLamp.size() - 1);
					this.svgid = "pl" + equipmentLamp.PhysicsPortLampIndex;
				}
				// 椭圆
				if (shapeType.equals("Ellipse")) {
					ShapeItem tempitem = null;
					tempitem = new ShapeItem(visioChart);
					if (!this.svgid.equals("")) {
						tempitem.setSvid(this.svgid);
					}
					tempitem.setFillcolor(crFillColor);
					tempitem.setLinecolor(crLineColor);
					tempitem.setLinewidth((int) lLineWith);
					tempitem.setItemid("g1");
					tempitem.setSvgtype(3);
					tempitem.setEllipse(translatex + lWidth / 2.0 + "",
							translatey + lHeight / 2.0 + "", lWidth / 2.0 + "",
							lHeight / 2.0 + "");
				}
				List<Node> NodeTo_lists = null;
				
				xpath = factory.createXPath(GeomIx + "/vdx:*");  
		        xpath.setNamespaceURIs(namespaceMap);
				NodeTo_lists = xpath.selectNodes(shape);
				vsGeometry[] geometrys;
				int geometry_pos = 0;
				// 计算角度
//				if (fFlipX.equals("1")) {
//					fAngle2 += 90;
//				}
//				if (fFlipY.equals("1")) {
//					fAngle2 += 180;
//				}
				// 文字处理
				svFont sfont = GetShapeText(shape);
				if (sfont != null && sfont.val != null && !sfont.val.equals("")) {

					ShapeItem tempitem = null;
					tempitem = new ShapeItem(visioChart);
					tempitem.setEcx(translatex  +"");
					tempitem.setEcy(translatey + lHeight / 2.0 + "");
					tempitem.setFillcolor(crTextColor);
					tempitem.setLinecolor(crLineColor);
					tempitem.setItemid("g1");
					tempitem.setSvgtype(10);
					tempitem.setsFont(sfont.fontname, sfont.fontsize + "em",
							sfont.textanchor);
					tempitem.setValue(sfont.val);

				} else {
					if (NodeTo_lists != null) {
						if (!TestShapeGeomName(NodeTo_lists)) {

							// 处理多Goem情况 非Ellipse与InfiniteLine
							geometrys = new vsGeometry[NodeTo_lists.size()];
							int nums = GetGeometryFromNodeList(NodeTo_lists,
									geometrys);
							DrawGoem.PageScale = PageScale;
							DrawGoem.Angle = fAngle2;
							DrawGoem.PixPerInch = this.PixPerInch;
							DrawGoem.sDL = this.sDL;
							DrawGoem.GoemWidth = lWidth;
							DrawGoem.GoemHeight = lHeight;
							DrawGoem.svgchart = visioChart;
							DrawGoem.x1 = translatex;
							DrawGoem.y1 = translatey;
							DrawGoem.lLineWith = lLineWith;
							DrawGoem.svid = this.svgid;
							DrawGoem.crLineColor = crLineColor;
							DrawGoem.crFillColor = crFillColor;
							DrawGoem.FlipX=fFlipX;
							DrawGoem.FlipY=fFlipY;

							String vspath = DrawGoem.DrawPathTo(geometrys);

						} else {
							// 处理 Ellipse与InfiniteLine 这两种情况，会只有一行数据没有MoveTo
						}
					} else {

						if (shapeType.equals("Rectangle")) {
							ShapeItem tempitem = null;
							tempitem = new ShapeItem(visioChart);
							if (!this.svgid.equals("")) {
								tempitem.setSvid(this.svgid);
							}
							tempitem.setFillcolor(crFillColor);
							tempitem.setLinecolor(crLineColor);
							tempitem.setLinewidth((int) lLineWith);
							tempitem.setItemid("g1");
							tempitem.setSvgtype(4);
							tempitem.setRect(translatex + "", translatey + "",
									lWidth + "", lHeight + "");
						}
					}
				}
				
				// 创建端口的rect
				if(customtype == 1)
				{
				ShapeItem tempitem = null;
				tempitem = new ShapeItem(visioChart);
				tempitem.setSvid("c" + PhysicsPortIndex);
				tempitem.setFillcolor("none");
				tempitem.setLinecolor("none");
				tempitem.setLinewidth(1);
				tempitem.setItemid("g1");
				tempitem.setCssclass("cc1");
				tempitem.setSvgtype(4);
				tempitem.setRect(translatex + "", translatey + "", lWidth
						+ "", lHeight + "");
				}

				if (lHeight == 0) {
					// Getting shape's begin & end coordinates
					lBeginX = Measure2Pix(xform1d_node, "BeginX");
					lBeginY = Measure2Pix(xform1d_node, "BeginY");
					lEndX = Measure2Pix(xform1d_node, "EndX");
					lEndY = Measure2Pix(xform1d_node, "EndY");

					if (lEndX != lBeginX) {
						lHeight = Math.abs(lEndX - lBeginX);
					} else if (lEndY != lBeginY) {
						lHeight = Math.abs(lEndY - lBeginY);
					} else {
						lHeight = 0;
					}

				}
				// Applying shape's colors, pens etc.

				if (sFillPattern == null || sFillPattern.equals("")) {
					sFillPattern = "0";
				}

				if (Short.parseShort(sFillPattern) > 1) {
					// oBox.setBrush(new
					// MindFusion.FlowChartX.LinearGradientBrush(crFillColor,
					// crFillColor2, 90));
				} else {
					// oBox.setBrush(new
					// MindFusion.FlowChartX.SolidBrush(crFillColor));
				}

				if (fFlipX.equals("1")) {
					// float nLeft = oBox.getBoundingRect().Left;
					// float nTop = oBox.getBoundingRect().Top;
					// float nWidth = oBox.getBoundingRect().getWidth();
					// float nheight = oBox.getBoundingRect().getHeight();
					// oBox.Resize(nheight, nWidth);
					// oBox.Move((nWidth - nheight) / 2 + nLeft, -(nWidth -
					// nheight) / 2 + nTop);

				}

				// if (sPenStyle != null)
				// {
				// oBox.setPenDashStyle(String2DashStyle(sPenStyle));
				// }
				// OnBoxImported(oBox, sShapeName, "");

			}

			// if the shape is GROUP
			if (lShapeTypes.contains("GROUP")) {
				if (!lShapeTypes.contains("ENTITY")
						&& !lShapeTypes.contains("OVAL_PROCESS")) {
					// oBox.setTransparent(true);
					// oBox.setFillColor(Color.FromArgb(255, 255, 255));
				}

				// oGroup = null;
				// oGroup = pChart.CreateGroup(oBox);

				// Saving group's measures for future use
				lGroupY = lY;
				lGroupX = lX;
				lGroupPinY = lPinY;
				lGroupPinX = lPinX;

				// Scanning for group members and creating it using recursive
				// call

				List<Node> grshape_list = null;
				xpath = factory.createXPath("vdx:Shapes/vdx:Shape");  
		        xpath.setNamespaceURIs(namespaceMap);
				grshape_list = xpath.selectNodes(shape);
				// shape.SelectNodes(, ns);
				for (Node grshape: grshape_list) {
					sTemp = String.format("%1$s", ShapeType);


					if (lShapeTypes.contains("ENTITY"))
					// if ( lShapeType & ST_ENTITY )
					{
						sTemp = "entity";
					}

					if (lShapeTypes.contains("ENTITY2"))
					// if ( lShapeType & ST_ENTITY2 )
					{
						sTemp = "entity 2";
					}

					if (lShapeTypes.contains("CONNECTOR"))
					// if ( lShapeType & ST_CONNECTOR )
					{
						sTemp = "arrow";
					}
					GetShapeRef(grshape, sTemp, true);
				}

				// Raising 'GroupImported' event
				// OnGroupImported(oGroup);
			}

		} catch (Exception ex) {
			
			System.out.println("GetGoemRef"+ex.getMessage());
		}
		return true;
	}

	/**
	 * Get styled text from Visio shape
	 * 
	 * @param shape
	 *            XML node of the shape
	 * @return Styled text of the Visio shape
	 */
	private svFont GetShapeText(Node shape) {
		String sText = "", sTextAttr = "", sWholeText = "", sStyle = "", sPara = "", sAlign = "", sTemp = "";
		int lStyle = 0, attr_count = 0, LineCount = 0;
		List<Node> attr_list = null;
		List<Node> text_list = null;
		Node attr_node = null;
		svFont sfont = new svFont();
		// StringAlignment TextAlignment = StringAlignment.Center;
		boolean IsRight = false;
		try {

			
			xpath = factory.createXPath("vdx:Text/vdx:cp");  
	        xpath.setNamespaceURIs(namespaceMap);
			attr_list = xpath.selectNodes(shape);
					//(NodeList) xpath.evaluate("vdx:Text/vdx:cp", shape,
					//XPathConstants.NODESET);
			xpath = factory.createXPath("vdx:Text/text()");  
	        xpath.setNamespaceURIs(namespaceMap);
			text_list = xpath.selectNodes(shape);
					//(NodeList) xpath.evaluate("vdx:Text/text()", shape,
					//XPathConstants.NODESET);

			sPara = GetShapeAttr("vdx:Text/vdx:pp", "IX", shape);

			if (sPara != null) {
				sTemp = GetShapeAttr(String.format(
						"vdx:Para[@IX='%1$s']/vdx:HorzAlign", sPara), "", shape);
				if (sTemp != null) {
					sAlign = sTemp;
				}
			}
			// 样式及text
			LineCount = text_list.size();

			for (Node text_node: text_list) {
				if (text_node == null) {
					attr_count++;
					continue;
				}

				sText = "";
				//InnerText
				sText = text_node.getText();
				sText = sText.replace("?", "\"");
				sText = sText.replace("?", "\"");

				attr_node = attr_list.get(attr_count);
				if (attr_node != null) {

					sTextAttr = ((Element) attr_node).attributeValue("IX");
					sStyle = GetShapeAttr(String.format(
							"vdx:Char[@IX='%1$s']/vdx:Style", sTextAttr), "",
							shape);

					if (sStyle == null) {
						lStyle = 0;
					} else {
						lStyle = Short.parseShort(sStyle);
					}

					if ((lStyle & 0x1) == 0x1) {
						sfont.fontstyle = 1;

					}

					if ((lStyle & 0x2) == 0x2) {
						sfont.fontstyle = 2;

					}

					if ((lStyle & 0x4) == 0x4) {
						sfont.fontstyle = 3;
					}

				}
				sWholeText += sText;
				attr_count++;

			}

			sfont.val = sWholeText;
			// 对齐
			sfont.textanchor = "start";
			if (sAlign.equals("0")) {
				sfont.textanchor = "start";
			} else if (sAlign.equals("1")) {
				sfont.textanchor = "start"; // middle 强制 
			} else if (sAlign.equals("2")) {
				sfont.textanchor = "end";
			} else {
				sfont.textanchor = "start";
			}

			if (LineCount > 1) {
				sfont.textanchor = "start";
			}
			String sSize, sFontID = null, sFontName, sStrikethru;
			double ftSize = 0;
			boolean ischi = HaveChineseCode(sWholeText);
			if (ischi) {
				sFontID = GetShapeAttr("vdx:Char/vdx:AsianFont", "", shape);
			} else {
				sFontID = GetShapeAttr("vdx:Char/vdx:Font", "", shape);
			}
			if (sFontID == null) {
				return null;
			}

			sFontName = GetShapeAttr(
					String.format("vdx:FaceName[@ID='%1$s']", sFontID), "Name",
					facesRoot);
			if (sFontName == null) {
				return null;
			}
			sfont.fontname = sFontName;
			sSize = GetShapeAttr("vdx:Char/vdx:Size", "", shape);
			if (sSize == null) {
				return null;
			}

			ftSize = Double.parseDouble(sSize);
			// float fsize=(float)ftSize * 72;
			float fsize = (float) ftSize * 72/16;//除以16转换为em
			sfont.fontsize =  fsize;

		} catch (Exception ex) {
			System.out.println("GetShapeText"+ex.getMessage());

		}

		return sfont;
	}

	private int GetGeometryFromNodeList(List<Node> NodeLists,
			vsGeometry[] geometrys) {
		int geometry_pos = 0;
		for (Node Node_list: NodeLists) {
			if (((Element)Node_list).attributeCount()>0 ) {
				// string IX_temp = Node_list.Attributes["IX"].Value.ToString();
				String IX_temp = GetShapeAttr("", "IX", Node_list);
				if ((IX_temp != null) && (!IX_temp.equals(""))) {
					String nodename = Node_list.getName();
					geometrys[geometry_pos] = new vsGeometry();
					geometrys[geometry_pos].name = nodename;
					geometrys[geometry_pos].x = (float) Unit2Pix(sDL, "MM",
							GetShapeAttr("vdx:X", "", Node_list));
					geometrys[geometry_pos].y = (float) Unit2Pix(sDL, "MM",
							GetShapeAttr("vdx:Y", "", Node_list));
					if (nodename.equals("EllipticalArcTo")) {
						geometrys[geometry_pos].a = GetShapeAttr("vdx:A", "",
								Node_list);
						geometrys[geometry_pos].b = GetShapeAttr("vdx:B", "",
								Node_list);
						geometrys[geometry_pos].c = GetShapeAttr("vdx:C", "",
								Node_list);
						geometrys[geometry_pos].d = GetShapeAttr("vdx:D", "",
								Node_list);
						geometrys[geometry_pos].e = GetShapeAttr("vdx:E", "",
								Node_list);
					} else if (nodename.equals("PolylineTo")) {
						geometrys[geometry_pos].a = GetShapeAttr("vdx:A", "",
								Node_list);
					} else if (nodename.equals("ArcTo")) {
						geometrys[geometry_pos].a = GetShapeAttr("vdx:A", "",
								Node_list);
					}
					geometry_pos++;
				}
			}
		}
		return geometry_pos;
	}

	/**
	 * Converts Visio predefined shape
	 * 
	 * @param sVisioShape
	 *            Visio shape type
	 * @return shape reference or [null] if some error occured
	 */
	private String VisioShape2PredefinedShape(String sVisioShape) {
		String Result = "Rectangle";

		try {
			if ((sVisioShape.equals("")) || (sVisioShape == null)) {
				sVisioShape = "Process";
			}

			sVisioShape = sVisioShape.trim();
			sVisioShape = sVisioShape.toLowerCase();

			if (sVisioShape.equals("process")) {
				Result = "Rectangle";
			} else if (sVisioShape.indexOf("person") >= 0) {
				Result = "Actor";
			} else if (sVisioShape.indexOf("45 degree single") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("foreign") >= 0) {
				Result = "Rectangle";
			} else if (sVisioShape.indexOf("45 degree tail") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("60 degree single") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("curved arrow") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("60 degree tail") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("45 degree double") >= 0) {
				Result = "Arrow5";
			} else if (sVisioShape.indexOf("60 degree double") >= 0) {
				Result = "Arrow5";
			} else if (sVisioShape.indexOf("2-d double") >= 0) {
				Result = "Arrow5";
			} else if (sVisioShape.indexOf("2-d single") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("1-d double") >= 0) {
				Result = "Arrow5";
			} else if (sVisioShape.indexOf("1-d open end") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("1-d single") >= 0) {
				Result = "Arrow3";
			} else if (sVisioShape.indexOf("double flexi-arrow") >= 0) {
				Result = "Arrow5";
			} else if (sVisioShape.indexOf("flexi-arrow") >= 0) {
				Result = "Arrow4";
			} else if (sVisioShape.indexOf("fancy arrow") >= 0) {
				Result = "Arrow4";
			} else if (sVisioShape.indexOf("loop limit") >= 0) {
				Result = "BeginLoop";
			} else if (sVisioShape.indexOf("object") >= 0) {
				Result = "Cloud";
			} else if (sVisioShape.indexOf("preparation") >= 0) {
				Result = "DataTransmition";
			} else if (sVisioShape.indexOf("stored data") >= 0) {
				Result = "DDelay";
			} else if (sVisioShape.indexOf("diamond") >= 0) {
				Result = "Decision";
			} else if (sVisioShape.indexOf("decision") >= 0) {
				Result = "Decision";
			} else if (sVisioShape.indexOf("display") >= 0) {
				Result = "Display";
			} else if (sVisioShape.indexOf("document") >= 0) {
				Result = "Document";
			} else if (sVisioShape.indexOf("control transfer") >= 0) {
				Result = "Interrupt";
			} else if (sVisioShape.indexOf("lined document") >= 0) {
				Result = "LinedDocument";
			} else if (sVisioShape.indexOf("manual operation") >= 0) {
				Result = "ManualOperation";
			} else if (sVisioShape.indexOf("extract") >= 0) {
				Result = "Alternative";
			} else if (sVisioShape.indexOf("file") >= 0) {
				Result = "File";
			} else if (sVisioShape.indexOf("sequential data") >= 0) {
				Result = "Tape";
			} else if (sVisioShape.indexOf("terminator") >= 0) {
				Result = "Terminator";
			} else if (sVisioShape.equals("data")) {
				Result = "Save";
			} else if (sVisioShape.indexOf("internal storage") >= 0) {
				Result = "InternalStorage";
			} else if (sVisioShape.indexOf("triangle") >= 0) {
				Result = "Alternative";
				// PinY*=2;
			} else if (sVisioShape.indexOf("heptagon") >= 0) {
				Result = "Heptagon";
			} else if (sVisioShape.indexOf("hexagon") >= 0) {
				Result = "Decision2";

			} else if (sVisioShape.indexOf("pentagon") >= 0) {
				Result = "Pentagon";
				// PinY*=1.5;
			} else if (sVisioShape.indexOf("octagon") >= 0) {
				Result = "Octagon";

			} else if (sVisioShape.indexOf("cross") >= 0) {
				Result = "Cross";
			} else if (sVisioShape.equals("star")) {
				Result = "Star";
			} else if (sVisioShape.indexOf("circle") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.indexOf("ellipse") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.indexOf("rounded rectangle") >= 0) {
				Result = "RoundRect";
			} else if (sVisioShape.indexOf("predefined process") >= 0) {
				Result = "Procedure";
			} else if (sVisioShape.indexOf("direct data") >= 0) {
				Result = "DirectAccessStorage";
			} else if (sVisioShape.indexOf("manual input") >= 0) {
				Result = "Input";
			} else if (sVisioShape.indexOf("card") >= 0) {
				Result = "PunchedCard";
			} else if (sVisioShape.indexOf("paper tape") >= 0) {
				Result = "Microform";
			} else if (sVisioShape.indexOf("on-page reference") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.indexOf("off-page reference") >= 0) {
				Result = "OffpageReference";
			} else if (sVisioShape.indexOf("note") >= 0) {
				Result = "File";
			} else if (sVisioShape.indexOf("data process") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.indexOf("multiple process") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("state")) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("start state")) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("stop state 2")) {
				Result = "Merge";
			} else if (sVisioShape.equals("stop state")) {
				Result = "Ellipse";
			} else if (sVisioShape.indexOf("multi state") >= 0) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("entity")) {
				Result = "Rectangle";
			} else if (sVisioShape.equals("oval process")) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("category")) {
				Result = "Ellipse";
			} else if (sVisioShape.equals("entity relationship")) {
				Result = "Decision";
			} else {
				Result = "Rectangle";
			}

		} catch (RuntimeException ex) {
			Result = null;
		}
		return Result;

	}

	/**
	 * Gets color value of the Visio shape selected
	 * 
	 * @param shape
	 *            XML node of the shape
	 * @param StyleID
	 *            String ID of the style to be used
	 * @param ColorName
	 *            String name of the color to be converted
	 * @return Color object
	 */
	private String GetColor(Node shape, String StyleID, String ColorName) {
		return GetColor(shape, StyleID, ColorName, null, null, null);
	}

	/**
	 * Gets color value of the Visio shape selected
	 * 
	 * @param shape
	 *            XML node of the shape
	 * @param StyleID
	 *            String ID of the style to be used
	 * @param ColorName
	 *            String name of the color to be converted
	 * @param TransparencyName
	 *            String name of the transparency to be combined with color
	 * @return Color object
	 */
	private String GetColor(Node shape, String StyleID, String ColorName,
			String TransparencyName) {
		return GetColor(shape, StyleID, ColorName, TransparencyName, null, null);
	}

	/**
	 * Gets color value of the Visio shape selected
	 * 
	 * @param shape
	 *            XML node of the shape
	 * @param StyleID
	 *            String ID of the style to be used
	 * @param ColorName
	 *            String name of the color to be converted
	 * @param TransparencyName
	 *            String name of the transparency to be combined with color
	 * @param ColorValue
	 *            #RGB value of the color
	 * @return Color object
	 */
	private String GetColor(Node shape, String StyleID, String ColorName,
			String TransparencyName, String ColorValue, String altStyleID) {
		String sColor = "", sTemp = "", sTransparency = "";
		String crColor = "";
		int iR = 0, iG = 0, iB = 0, iA = 255;

		try {

			if (ColorValue == null) {
				sColor = GetShapeAttr(ColorName, "", shape);
			} else {
				sColor = ColorValue;
			}

			if (sColor == null) {
				sTemp = String.format("vdx:StyleSheet[@ID='%1$s']/%2$s",
						StyleID, ColorName);
				sColor = GetShapeAttr(sTemp, "", stylesRoot);

			}

			if (sColor == null) {
				sTemp = String.format("vdx:StyleSheet[@ID='%1$s']/%2$s",
						altStyleID, ColorName);
				sColor = GetShapeAttr(sTemp, "", stylesRoot);

			}

			if (sColor == null) {
				return crColor;
			}

			if (sColor.charAt(0) != '#') {

				sTemp = String.format("vdx:ColorEntry[@IX='%1$s']", sColor);
				sColor = GetShapeAttr(sTemp, "RGB", colorsRoot);

			}

			if (sColor.length() <= 6) {
				return crColor;
			}
			crColor = sColor;
			
			//透明处理
			
//			 if (TransparencyName != null)
//			 {
//			 sTransparency = GetShapeAttr(TransparencyName, "", shape);
//			 if (sTransparency != null)
//			 {
//			 sTransparency = sTransparency.replace(".", ",");
//			 double dA = Double.parseDouble(sTransparency);
//			 iA = (int)((10 - dA) * 25.5);
//			 }
//			
//			 }
			//
			//
			//
			// iR = Short.parseShort(sColor.substring(1, 3), 16);
			// iG = Short.parseShort(sColor.substring(3, 5), 16);
			// iB = Short.parseShort(sColor.substring(5, 7), 16);
			// crColor = new Color();
			// crColor = Color.Empty;
			// crColor = Color.FromArgb(iA, iR, iG, iB);

		} catch (RuntimeException ex) {
			crColor = "";
		}

		return crColor;

	}

	/**
	 * Gets Visio style from its name
	 * 
	 * @param StyleName
	 *            Style name
	 * @return String[] style
	 */
	private String[] GetStyle(Node shape, String StyleName, String ExtraStyle) {

		String[] Result = { "", "" };
		String sTemp = "", sTemp2;
		Node style_node = null;
		Element element = (Element) shape;

		try {
			if (element.attributeValue(StyleName) != null
					&& !element.attributeValue(StyleName).equals("")) {
				Result[0] = element.attributeValue(StyleName);
			} else {
				if (sMasterID.equals("")) {
					xpath = factory.createXPath("vdx:PageSheet");  
			        xpath.setNamespaceURIs(namespaceMap);
					style_node = xpath.selectSingleNode(current_page);
							
				} else {
					xpath = factory.createXPath(String.format(
							"vdx:Master[@ID='%1$s']/vdx:Shapes/vdx:Shape",
							sMasterID));  
			        xpath.setNamespaceURIs(namespaceMap);
					style_node =xpath.selectSingleNode(mastersRoot);
							
				}

				if (style_node == null) {
					return null;
				}

				Result[0] = ((Element) style_node).attributeValue(StyleName);

			}

			sTemp = String.format("vdx:StyleSheet[@ID='%1$s']", Result);
			sTemp2 = GetShapeAttr(sTemp, StyleName, stylesRoot);

			if (sTemp2 != null && !sTemp2.equals("")) {
				if (!sTemp2.equals("0")) {
					Result[1] = sTemp2;
				}
			}

		} catch (Exception ex) {
			Result = null;
		}
		return Result;
	}

	/**
	 * 角度计算
	 * 
	 * @param AngleUnit
	 * @param AngleValue
	 * @return
	 */
	private double Angle2Deg(String AngleUnit, String AngleValue) {

		double fResult = 0, fAngle = 0;
		try {

			if (AngleValue == null || AngleValue.equals("")) {
				return fResult;
			}

			if (AngleValue.equals("0")) {
				return fResult;
			}

			if (AngleUnit != null) {
				AngleUnit = AngleUnit.trim();
				AngleUnit = AngleUnit.toLowerCase();
			} else {
				AngleUnit = "rad";
			}

			// AngleValue = AngleValue.replace(".", sSeparator);

			if (AngleUnit.equals("am") || AngleUnit.equals("as")
					|| AngleUnit.equals("ad") || AngleUnit.equals("da")
					|| AngleUnit.equals("rad") || AngleUnit.equals("deg")) {
				fAngle = (Double.parseDouble(AngleValue) / Math.PI) * 180;
			} else {
				fAngle = (Double.parseDouble(AngleValue) / Math.PI) * 180;

			}
			fResult=fAngle;
//			if (fAngle > 0) {
//				fResult = 360 - Math.abs(fAngle);
//			} else {
//				fResult = Math.abs(fAngle);
//			}

		} catch (RuntimeException ex) {
			fResult = 0;
		}

		return fResult;
	}

	/**
	 * 判断是否有终点即闭合点 true 是没有 false 有
	 * 
	 * @param shape
	 * @param ShapeType
	 * @param IX
	 * @return
	 */
	private boolean TestNoFillRef(Node shape, String ShapeType, String IX) {
		String GeomIx = "vdx:Geom";
		if (!IX.equals("")) {
			GeomIx = String.format("vdx:Geom[@IX='%1$s']", IX);
		}
		String Text = GetShapeAttr("vdx:Text", "", shape);
		if (Text != null && !Text.equals("")) {
			return false;
		}
		String NoFill = GetShapeAttr(GeomIx + "/vdx:NoFill", "", shape);
		if (NoFill != null && NoFill.equals("1")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断shape 为Ellipse 和InfiniteLine
	 * 
	 * @param nodeList
	 * @return
	 */
	private boolean TestShapeGeomName(List<Node> nodeList) {
		for (Node node: nodeList) {
			if (node.getName().equals("Ellipse")
					|| node.getName().equals("InfiniteLine")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Getting VDX node attribute or text
	 * 
	 * @param AttrPath
	 *            String XPath expression to found child node of the [shape]
	 * @param AttrName
	 *            String Name of the attribute or "" for [Text]
	 * @param shape
	 *            Parent node reference or [null] if root node is parent
	 * @return [true] if successfull [false] otherwise
	 */

	private String GetShapeAttr(String AttrPath, String AttrName, Node shape) {
		String sTemp = null;
		try {

			Node temp_node = null;

			if (shape == null) {
				xpath = factory.createXPath(AttrPath);  
		        xpath.setNamespaceURIs(namespaceMap);
		        temp_node= xpath.selectSingleNode(root);
				
			} else {
				if (!AttrPath.equals("")) {
					xpath = factory.createXPath(AttrPath);  
			        xpath.setNamespaceURIs(namespaceMap);
					temp_node = xpath.selectSingleNode(shape);
				} else {
					temp_node = shape;
				}
			}
			if (temp_node == null) {
				return null;
			}

			if (AttrName.equals("")) {
				// InnerText
				sTemp = temp_node.getText();
			} else {
				sTemp = ((Element) temp_node).attributeValue(AttrName);
			}

		} catch (Exception ex) {
			sTemp = null;
		}

		return sTemp;
	}

	/**
	 * Getting VDX node attribute or text
	 * 
	 * @param AttrPath
	 *            String XPath expression to found child node of the [shape]
	 * @param AttrName
	 *            String Name of the attribute or "" for [Text]
	 * @return [true] if successfull [false] otherwise
	 */
	private String GetShapeAttr(String AttrPath, String AttrName) {
		return GetShapeAttr(AttrPath, AttrName, null);
	}

	/**
	 * Convert shape node measures to pixels
	 * 
	 * @param xform_shape
	 *            XML shape node reference
	 * @param MeasureName
	 *            String name of the measure to be converted
	 * @return Measure value in pixels
	 */
	private float Measure2Pix(Node xform_shape, String MeasureName) {
		String sTemp = "", sUnit = "";

		try {
			MeasureName = "vdx:" + MeasureName;
			xpath = factory.createXPath(MeasureName);  
	        xpath.setNamespaceURIs(namespaceMap);
	        Node measure_node = xpath.selectSingleNode(xform_shape);
			

			if (measure_node == null) {
				return -1;
			}
			String tempUnit = ((Element) measure_node).attributeValue("Unit");

			if (tempUnit == null || tempUnit.equals("")) {
				sUnit = sDL;
			} else {
				sUnit = tempUnit;
			}
			// InnerText
			sTemp = ((Element)measure_node).getText();

		} catch (Exception ex) {
			sTemp = ex.getMessage();
			return -1;
		}
		return (float) Unit2Pix(sUnit, sDL, sTemp);
	}

	/**
	 * Converts string dimension value from unit to pixels
	 * 
	 * @param Unit
	 *            Name of the unit of measures
	 * @param DefaultUnit
	 *            >Name of the DEFAULT unit of measures
	 * @param Value
	 *            String value of the dimensions
	 * @return Dimension value in pixels
	 */
	private double Unit2Pix(String Unit, String DefaultUnit, String Value) {
		String sUnit = Unit, sDefaultUnit = DefaultUnit;
		double fVal, fRes;

		// Setting decimal separator to ','
		// Value = Value.replace(".", sSeparator);
		fVal = Double.parseDouble(Value);

		sUnit = sUnit.toLowerCase();
		sDefaultUnit = sDefaultUnit.toLowerCase();

		if (sUnit.equals("dl")) {
			sUnit = sDefaultUnit;
		}

		if (sUnit.equals("in")) {
			fRes = fVal * PixPerInch;
		} else if (sUnit.equals("mm")) {
			fRes = (double) (fVal / 24.4) * PixPerInch;
		} else if (sUnit.equals("cm")) {
			fRes = (double) (fVal / 2.44) * PixPerInch;
		} else {
			fRes = 0;
		}

		if (!sUnit.equals(sDefaultUnit)) {
			fRes = fRes * PageScale;
		}

		return fRes;
	}

	/**
	 * 是否含有中文
	 * 
	 * @param testString
	 * @return
	 */
	protected final boolean HaveChineseCode(String testString) {
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
		Matcher matc = pattern.matcher(testString);
		if (matc.find()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Processing Visio shape converting it into Flowchart.NET node
	 * 
	 * @param shape
	 *            Visio shape's XML node
	 * @param ShapeType
	 *            String value of the shape type
	 * @param group
	 *            Reference to parent group ( if any exists)
	 * @return Returns [true] if successfull [false] otherwise
	 */
	private String GetPropAttr(String LabelName, Node shape) {
		for (int i = 1; i < 10; i++) {

			String AttrPath = String
					.format("vdx:Prop[@ID='%1$s']/vdx:Label", i);
			String Prop_node = GetShapeAttr(AttrPath, "", shape);
			if ((Prop_node != null) && (!Prop_node.equals(""))) {
				if (LabelName.equals(Prop_node)) {
					AttrPath = String.format("vdx:Prop[@ID='%1$s']/vdx:Value",
							i);
					Prop_node = GetShapeAttr(AttrPath, "", shape);
					if (Prop_node != null) {
						return Prop_node;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param shape
	 * @return 0 no custom 1 端口 2 端口灯 3 设备信息
	 */
	private int customShape(Node shape) {
		int result = 0;
		String SysOid = GetPropAttr("SysOid", shape);
		if (SysOid != null && !SysOid.equals("")) {
			pEquipmentInfo = new EquipmentInfo();
			pEquipmentInfo.Sysoid = SysOid;
			String AttrTemp;
			// //高度
			// pEquipmentInfo.setHeight((int)oBox.getBoundingRect().getHeight());
			// //宽度
			// pEquipmentInfo.setWidth((int)oBox.getBoundingRect().getWidth());
			// //模块数
			AttrTemp = GetPropAttr("模块数", shape);
			if (AttrTemp != null && !AttrTemp.equals("")) {
				pEquipmentInfo.Sockets = Short.parseShort(AttrTemp);
			} else {
				AttrTemp = GetPropAttr("Sockets", shape);
				if (AttrTemp != null && !AttrTemp.equals("")) {
					pEquipmentInfo.Sockets = Short.parseShort(AttrTemp);
				}
			}
			// //端口数
			AttrTemp = GetPropAttr("端口数", shape);
			if (AttrTemp != null && !AttrTemp.equals("")) {
				pEquipmentInfo.Ports = Short.parseShort(AttrTemp);
			} else {
				AttrTemp = GetPropAttr("Ports", shape);
				if (AttrTemp != null && !AttrTemp.equals("")) {
					pEquipmentInfo.Ports = Short.parseShort(AttrTemp);
				}
			}
			result = 3;
		}

		String PortIndex = GetPropAttr("端口号", shape);
		String PortIndex1 = GetPropAttr("PortIndex", shape);

		if ((PortIndex != null && !PortIndex.equals(""))
				|| (PortIndex1 != null && !PortIndex1.equals(""))) {
			if (PortIndex1 != null && !PortIndex1.equals("")) {
				PortIndex = PortIndex1;
			}
			EquipmentPort m_port = new EquipmentPort();
			m_port.PhysicsPortIndex = Short.parseShort(PortIndex);
			// m_port.Alias = oBox.getTag().toString();
			pEquipmentPort.add(m_port.clone());
			result = 1;
		}

		String PortLampIndex = GetPropAttr("端口灯号", shape);
		String PortLampIndex1 = GetPropAttr("PortLampIndex", shape);

		if ((PortLampIndex != null && !PortLampIndex.equals(""))
				|| (PortLampIndex1 != null && !PortLampIndex1.equals(""))) {
			if (PortLampIndex1 != null && !PortLampIndex1.equals("")) {
				PortLampIndex = PortLampIndex1;
			}
			EquipmentLamp m_portlamp = new EquipmentLamp();
			m_portlamp.PhysicsPortLampIndex = Short.parseShort(PortLampIndex);
			// m_portlamp.Alias = oBox.getTag().toString();
			pEquipmentLamp.add(m_portlamp.clone());
			result = 2;
		}

		String ContainerIndex = GetPropAttr("容器号", shape);
		String ContainerIndex1 = GetPropAttr("ContainerIndex", shape);

		if ((ContainerIndex != null && !ContainerIndex.equals(""))
				|| (ContainerIndex1 != null && !ContainerIndex1.equals(""))) {
			if (ContainerIndex1 != null && !ContainerIndex1.equals("")) {
				ContainerIndex = ContainerIndex1;
			}
			EquipmentContainer m_equipmentcontainer = new EquipmentContainer();
			m_equipmentcontainer.ContainerIndex = Short
					.parseShort(ContainerIndex);
			// m_equipmentcontainer.Alias = oBox.getTag().toString();
			// m_equipmentcontainer.rect = oBox.getBoundingRect();
			pEquipmentContainer.add(m_equipmentcontainer.clone());
			result = 4;
		}
		return result;
	}
}
