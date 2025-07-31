package in.apcfss.common;

import java.awt.Color;

import com.itextpdf.text.List;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;



public class Itext_pdf_setting {
	int para_font = 5; 
	int head_font = 8;
	public int indent = 0;
	public int font_weight = Font.BOLD;
	
	public  PdfPTable table(int cols,int width) throws DocumentException{
		PdfPTable table = new PdfPTable(cols);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_MIDDLE);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.setTotalWidth(50);
/*
		float[] f=new float[]{0.15f};
		table.setWidths(f);*/
		
		table.setWidthPercentage(width);
		table.getDefaultCell().setBorder(0);
		 //table.getDefaultCell().setHorizontalAlignment(align);
		 table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
		 table.setTotalWidth(100);
		 table.getDefaultCell().setSpaceCharRatio(2);
		 
		 return table;
	}
	public  PdfPTable table(float[] width) throws DocumentException{
		PdfPTable table = new PdfPTable(width);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_MIDDLE);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		//table.setTotalWidth(50);
		table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
		
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(0);
		 table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
		 table.setTotalWidth(100);
		 table.getDefaultCell().setSpaceCharRatio(2);
		 
		 return table;
	}
	
	public int cell_border = Rectangle.NO_BORDER;
	public  PdfPCell addCellElement(Element ele,int colspan){
		PdfPCell innercell=new PdfPCell();
		
		innercell.addElement(ele);
		innercell.setHorizontalAlignment(Element.ALIGN_LEFT);
		innercell.setBorder(cell_border);
		innercell.setColspan(colspan);
		innercell.setMinimumHeight(4f);
		
		return innercell;
	}
	public int border = Rectangle.NO_BORDER;
	
	public  PdfPCell cell(String s,int colspan,int align,int font,int fontweight){
		PdfPCell  cell =new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA, font,fontweight,new Color(0, 0, 0))));
		cell.setHorizontalAlignment(align);
		cell.setBorder(border);
		cell.setColspan(colspan);
		cell.setMinimumHeight(6f);
		
		return cell;
	}
	
	public Paragraph para(String s,int fontsize,int align,int spacingBefore,int spacingAfter){
		Font ft = FontFactory.getFont(FontFactory.HELVETICA, fontsize);
		Paragraph paragraph1 = new Paragraph(s, ft);
		paragraph1.setAlignment(align);
		paragraph1.setSpacingAfter(spacingAfter);
		paragraph1.setSpacingBefore(spacingBefore);
		paragraph1.setIndentationLeft(this.indent);
		return paragraph1;
	}
	public static Font ft = FontFactory.getFont(FontFactory.HELVETICA, 10);
	public Paragraph CustomFontpara(String s,int align,int spacingBefore,int spacingAfter){
		
		Paragraph paragraph1 = new Paragraph(s, ft);
		paragraph1.setAlignment(align);
		paragraph1.setSpacingAfter(spacingAfter);
		paragraph1.setSpacingBefore(spacingBefore);
		paragraph1.setIndentationLeft(this.indent);
		return paragraph1;
	}
	
	public com.lowagie.text.List PdfList(boolean type){//com.lowagie.text.List.ORDERED
		com.lowagie.text.List list = new com.lowagie.text.List(type);
		return list;
	}
	
	public ListItem item(String s,int align,Font font){
		ListItem item = new ListItem();
		item.setAlignment(align);
        item.setFont(font);
		item.add(s);
        return item;
	}
	
}
