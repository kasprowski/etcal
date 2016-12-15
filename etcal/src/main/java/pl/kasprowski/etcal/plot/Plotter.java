package pl.kasprowski.etcal.plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import pl.kasprowski.etcal.calibration.RegressionData;
import pl.kasprowski.etcal.dataunits.DU2RDConverter;
import pl.kasprowski.etcal.dataunits.DataUnits;

public class Plotter {

	/**
	 * Displays a frame with a given image
	 * @param caption
	 * @param img
	 */
	public void displayPlot(String caption, BufferedImage img) {
		JFrame frame = new JFrame(caption);
		frame.setSize(img.getWidth(), img.getHeight());
		JLabel label = new JLabel(new ImageIcon(img));
		frame.add(label);
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Contructs an image containing data from two DataUnits
	 * Shows targets from both DataUnits
	 * @param dataUnits1
	 * @param dataUnits2
	 * @param sizeX
	 * @param sizeY
	 * @return
	 */
	public BufferedImage plot(DataUnits dataUnits1,DataUnits dataUnits2, double sizeX, double sizeY,
			int crossSize) {
		return plot(dataUnits1,dataUnits2,sizeX,sizeY,crossSize,true);
	}

	public BufferedImage plot(DataUnits dataUnits1,DataUnits dataUnits2, double sizeX, double sizeY,
			int crossSize, boolean showTargets) {
		return plot(dataUnits1,dataUnits2,sizeX,sizeY,crossSize,true,true);
	}
		public BufferedImage plot(DataUnits dataUnits1,DataUnits dataUnits2, double sizeX, double sizeY,
				int crossSize, boolean showTargets1, boolean showTargets2) {

		RegressionData data1 = DU2RDConverter.dataUnits2RegressionData(dataUnits1);
		RegressionData data2 = DU2RDConverter.dataUnits2RegressionData(dataUnits2);

		BufferedImage img = new BufferedImage((int)sizeX, (int)sizeY, BufferedImage.TYPE_INT_RGB);
		double maxX = 0;
		double maxY = 0;
		double minX = 10000;
		double minY = 10000;

		for(int i=0;i<sizeX;i++)
			for(int j=0;j<sizeY;j++)
				img.setRGB(i, j, 16777215);
		for(int i=0;i<data1.size();i++) {
			double x = data1.getY1(i);
			double y = data1.getY2(i);
			if(x>maxX) maxX = x;
			if(y>maxY) maxY = y;
			if(x<minX) minX = x;
			if(y<minY) minY = y;

			double xx = data2.getY1(i);
			double yy = data2.getY2(i);

			if(x>maxX) maxX = xx;
			if(y>maxY) maxY = yy;
			if(x<minX) minX = xx;
			if(y<minY) minY = yy;

		}
		double brX = (maxX-minX)/10;
		double brY = (maxY-minY)/10;

		maxX+=brX;
		maxY+=brY;
		minX-=brX;
		minY-=brY;

		Graphics gr = img.createGraphics();
		int s = crossSize;

		if(data1.size()==data2.size()) {
			for(int i=0;i<data2.size();i++) {	
				
				double x = 0;
				double y = 0;
				double xx = 0;
				double yy = 0;
				if(showTargets1 && showTargets2) {
					x = (data1.getY1(i) - minX)*sizeX/(maxX-minX);
					y = (data1.getY2(i) - minY)*sizeY/(maxY-minY);
					xx = (data2.getY1(i) - minX)*sizeX/(maxX-minX);
					yy = (data2.getY2(i) - minY)*sizeY/(maxY-minY);
				} else {
					x = (data1.getX(i)[0] - minX)*sizeX/(maxX-minX);
					y = (data1.getX(i)[1] - minY)*sizeY/(maxY-minY);
					xx = (data2.getX(i)[0] - minX)*sizeX/(maxX-minX);
					yy = (data2.getX(i)[1] - minY)*sizeY/(maxY-minY);
				}
				gr.setColor(Color.GREEN);
				gr.drawLine((int)x, (int)y, (int)xx, (int)yy);
			}
		}

		
		for(int i=0;i<data1.size();i++) {
			double x = 0;
			double y = 0;
			if(showTargets1) {
				x = (data1.getY1().get(i) - minX)*sizeX/(maxX-minX);
				y = (data1.getY2().get(i) - minY)*sizeY/(maxY-minY);
			} else {
				x = (data1.getX(i)[0] - minX)*sizeX/(maxX-minX);
				y = (data1.getX(i)[1] - minY)*sizeY/(maxY-minY);
			}
				
			//System.out.println("IN "+data.gtY1().get(i)+ " "+data.getY2().get(i));

			
			gr.setColor(Color.RED);	
			gr.drawLine((int)x-s, (int)y-s, (int)x+s, (int)y+s);
			gr.drawLine((int)x-s, (int)y+s, (int)x+s, (int)y-s);
		}
		for(int i=0;i<data2.size();i++) {
			double x = 0;
			double y = 0;
			if(showTargets2) {
				x = (data2.getY1().get(i) - minX)*sizeX/(maxX-minX);
				y = (data2.getY2().get(i) - minY)*sizeY/(maxY-minY);
			} else {
				x = (data2.getX(i)[0] - minX)*sizeX/(maxX-minX);
				y = (data2.getX(i)[1] - minY)*sizeY/(maxY-minY);
			}
			gr.setColor(Color.BLUE);
			gr.drawLine((int)x-s, (int)y, (int)x+s, (int)y);
			gr.drawLine((int)x, (int)y+s, (int)x, (int)y-s);
		}


		return img;

	}
}
