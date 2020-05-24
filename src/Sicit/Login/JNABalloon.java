/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package Sicit.Login;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.sun.jna.examples.BalloonManager;

/**
 * 用于产生提示气泡;
 * 如提示用户发送的是空消息,
 * 或者提示用户有新消息到达.
 * 
 * @author Timothy Wall, shinysky
 * 
 */
public class JNABalloon {
	
	private static final int ICON_SIZE = 48;

	private static class InfoIcon implements Icon {
		public int getIconHeight() {
			return ICON_SIZE;
		}

		public int getIconWidth() {
			return ICON_SIZE;
		}

		public void paintIcon(Component c, Graphics graphics, int x, int y) {
			Font font = UIManager.getFont("TextField.font");
			Graphics2D g = (Graphics2D) graphics.create(x, y, getIconWidth(),
					getIconHeight());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont(font.deriveFont(Font.BOLD, getIconWidth() * 3 / 4));
			g.setColor(Color.green.darker());
			final int SW = Math.max(getIconWidth() / 10, 4);
			g.setStroke(new BasicStroke(SW));
			g.drawArc(SW / 2, SW / 2, getIconWidth() - SW - 1, getIconHeight()
					- SW - 1, 0, 360);
			Rectangle2D bounds = font.getStringBounds("i", g
					.getFontRenderContext());
			g.drawString("i", Math.round((getIconWidth() - bounds.getWidth())
					/ 2 - getIconWidth() / 12), SW
					/ 2
					+ Math.round((getIconHeight() - bounds.getHeight()) / 2
							- bounds.getY() + getIconHeight() / 8));
			g.dispose();
		}
	}

	private MouseListener listener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			hidePopup(e);
		}
	};
	private JLabel content;
	private Popup popup;

	private void hidePopup(MouseEvent e) {
		e.getComponent().removeMouseListener(listener);
		if (popup != null)
			popup.hide();
	}

	public JNABalloon(String BALLOON_TEXT, JComponent owner, int X, int Y) {
		//System.setProperty("sun.java2d.noddraw", "true");
		content = new JLabel(BALLOON_TEXT);
		content.setIconTextGap(10);
		content.setBorder(new EmptyBorder(0, 8, 0, 8));
		content.setSize(content.getPreferredSize());
		content.setIcon(new InfoIcon());
		popup = BalloonManager.getBalloon(owner, content, X, Y);
		content.addMouseListener(listener);
		content.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent arg0) {
				System.out.println("content get focusssssssssss");
			}
			public void focusLost(FocusEvent fe) {
				System.out.println("content lost focusssssssssss");
				fe.getComponent().removeMouseListener(listener);
				if (popup != null)
					popup.hide();
			}
		});
	}

	public void showBalloon() {
		//TODO requestFocus(), 当失去焦点时自动消失.
		//目前有问题, 不知道原因.
		//content.requestFocusInWindow();
		//content.requestFocus(true);
		content.requestFocus();
		popup.show();
	}

	public Popup getBalloon() {
		return popup;
	}
}