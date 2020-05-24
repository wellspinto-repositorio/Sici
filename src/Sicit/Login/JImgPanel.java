package Sicit.Login;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

/*
 * Created on 2006-9-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * 可以显示背景图片的类,继承自JPanel
 * 
 * @author shinysky
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class JImgPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 * 
	 * @param img
	 *            背景图片
	 */
	public JImgPanel(Image img) {
		modal = false;
		img_mp = img;
	}
	public JImgPanel(Image img, Point ori, Dimension dim) {
		modal = true;
		img_mp = img;
		origin = ori;
		square = dim;
	}

	public void setBackImage(Image img) {
		img_mp = img;
		//System.out.println("setting BackImage");
	}

	/**
	 * 重载绘制组件的方法，绘制图像
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		/**
		 * 以下N行代码可以实现图片随窗口大小动态缩放(9.4) ^_^
		 */
/*		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();*/
		if(modal)//指定开始位置和绘图尺寸
			g.drawImage(img_mp, (int)origin.getX(), (int)origin.getY(), 
				(int)square.getWidth(), (int)square.getHeight(), this);
					//this.getWidth(), this.getHeight(), this);
		else
			g.drawImage(img_mp, 0, 0, 
					this.getWidth(), this.getHeight(), this);

		/**
		 * 注释以下条件判断句,可以在保持图片动态缩放的同时,不使滚动条出现 (既然这样就不用滚动面板算了!)
		 * 但是当两个滚动条(X方向和Y方向)都出现时,图像为原大小
		 */
		if (firstInvoked) {
			firstInvoked = false;
			getImageSizeAndSet();
			//System.out.println("image size : " + img_width + " * " + img_height);
		}
	}

	/**
	 * 获取图片尺寸并参照它设置面板首选尺寸
	 * 
	 */
	private void getImageSizeAndSet() {
		img_width = img_mp.getWidth(JImgPanel.this);
		img_height = img_mp.getHeight(JImgPanel.this);

		//System.out.println("In getImageSizeAndSet():" + img_width + " * " + img_height);
		/**
		 * 希望通过 if 判断来消除第一次载入图像时的闪烁 当然是在"闪烁是因为未得到图片正确尺寸"这个判断正确的前提下;
		 * But...有时还是有闪烁; 其实是因为绘图的原因,可以通过提前绘图来解决这个问题.
		 */
		if (img_width == -1 || img_height == -1) {
			this.setPreferredSize(new Dimension(800, 600));
			//System.out.println("Exception at getting size of image");

		} else
			this.setPreferredSize(new Dimension(img_width, img_height));
		//System.out.println("After setPreferredSize():" + img_width + " * "	+ img_height);
	}

	/**
	 * 是否第一次调用
	 */
	private boolean firstInvoked = true;
	/**
	 * 背景图片
	 */
	private Image img_mp;
	/**
	 * 图片高度
	 */
	private int img_height;
	/**
	 * 图片宽度
	 */
	private int img_width;
	/**
	 * 绘图模式
	 * false: default, 全部覆盖
	 * true: 指定开始位置和绘图尺寸
	 */
	private boolean modal;
	/**
	 * 开始绘图位置
	 */
	private Point origin;
	/**
	 * 绘图尺寸
	 */
	 private Dimension square;
}