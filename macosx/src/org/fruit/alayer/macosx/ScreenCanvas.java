/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.macosx;

import org.fruit.alayer.Color;
import org.fruit.alayer.ICanvas;
import org.fruit.alayer.IPen;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Utils;

public final class ScreenCanvas implements ICanvas {

	class ScreenCanvasPen implements IPen{
		
		IPen pen;
		
		public ScreenCanvasPen(){ 
			this.pen = new Pen();
			setFont(pen.getFont());
			setFontSize(pen.getFontSize());
			setThickness(pen.getThickness());
			setColor(pen.getColor());
			setStrokeStyle(pen.getStrokeStyle());
			setHeadStyle(pen.getHeadStyle()); 
		}
		
		public void setFont(String font){
			pen.setFont(font);
			AX.CGContextSelectFont(context, font, getFontSize(), 0);
		}
		public void setFontSize(int size){
			pen.setFontSize(size);
			AX.CGContextSelectFont(context, getFont(), size, 0);
		}
		public void setThickness(double value) {
			pen.setThickness(value);
			AX.CGContextSetLineWidth(context, (float)value);
		}
		public void setColor(Color color) {
			pen.setColor(color);
			float red = color.getRed() / 255.0f;
			float green = color.getGreen() / 255.0f;
			float blue = color.getBlue() / 255.0f;
			float alpha = color.getAlpha() / 255.0f;
			AX.CGContextSetRGBStrokeColor(context, red, green, blue, alpha);
			AX.CGContextSetRGBFillColor(context, red, green, blue, alpha);
		}
		public void setStrokeStyle(StrokeStyle style) {
			pen.setStrokeStyle(style);
			//throw new UnsupportedOperationException("Not yet implemented!");
		}
		public void setHeadStyle(HeadStyle style){ 
			pen.setHeadStyle(style);
			AX.CGContextSetLineCap(context, 2);
		}
		public Color getColor() { return pen.getColor(); }
		public double getThickness() { return pen.getThickness(); }
		public StrokeStyle getStrokeStyle() { return pen.getStrokeStyle(); }
		public int getFontSize() { return pen.getFontSize(); }
		public String getFont() { return pen.getFont();	}
		public HeadStyle getHeadStyle() { return pen.getHeadStyle(); }
	}
	
	long overlayWindow;
	long context;
	long mainScreen;
	double[] frame;
	IPen pen;
	
	public ScreenCanvas(){
		overlayWindow = AX.createOverlayWindow();
		mainScreen = AX.createMainScreen();
		float[] tmpFrame = AX.getScreenFrame(mainScreen);
		frame = new double[]{tmpFrame[0], tmpFrame[1], tmpFrame[2], tmpFrame[3]};
		context = AX.createCGContext(overlayWindow);
		Utils.Sleep(1);
		AX.setWindowFrame(overlayWindow, tmpFrame[0], tmpFrame[1], tmpFrame[2], tmpFrame[3]);
		pen = this.new ScreenCanvasPen();
		AX.CGContextClearRect(context, tmpFrame[0], tmpFrame[1], tmpFrame[2], tmpFrame[3]);
		AX.CGContextFlush(context);
	}
	
	public void begin() throws Exception {}
	public void end() throws Exception { AX.CGContextFlush(context); }

	public void line(double x1, double y1, double x2, double y2) {
		AX.CGContextStrokeLine(context, (float)x1, (float)(frame[3] - y1), (float) x2, (float)(frame[3] - y2));
	}

	public void rect(double x, double y, double width, double height, boolean fill) {
		if(fill)
			AX.CGContextFillRect(context, (float)x, (float)(frame[3] - y - height), (float)width, (float)height);
		else
			AX.CGContextStrokeRect(context, (float)x, (float)(frame[3] - y - height), (float)width, (float)height);
	}

	public void ellipse(double x, double y, double width, double height, boolean fill) {
		if(fill)
			AX.CGContextFillEllipseInRect(context, (float)x, (float)(frame[3] - y - height), (float)width, (float)height);
		else
			AX.CGContextStrokeEllipseInRect(context, (float)x, (float)(frame[3] - y - height), (float)width, (float)height);
	}

	public void text(double x, double y, String text) {
		AX.CGContextShowTextAtPoint(context, (float)x, (float)(frame[3] - y), text);
	}
	
	public void clear(double x, double y, double width, double height) {
		AX.CGContextClearRect(context, (float)x, (float)(frame[3] - y - height), (float)width, (float)height);
	}

	public void setPixelData(double x, double y, double width, double height,
			int[] data, int dataWidth, int dataHeight) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	
	public double getWidth(){ return frame[2]; }
	public double getX(){ return frame[0]; }
	public double getHeight(){ return frame[3];	}
	public double getY(){ return -frame[1];	}
	public IPen getPen() { return pen; }
}
