package aurelienribon.slidinglayout.demo;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ThePanel extends JPanel {
	private static final Color FG_COLOR = new Color(0xFFFFFF);
	private static final Color BG_COLOR = new Color(0x3B5998);
	private static final Color BORDER_COLOR = new Color(0x000000);

	private static final TweenManager tweenManager = SLAnimator.createTweenManager();
	private final JLabel label = new JLabel();
	private BufferedImage bgImg;
	private Runnable action;
	private boolean actionEnabled = true;
	private int borderThickness = 2;

	public ThePanel(String name, String imgPath) {
		setBackground(BG_COLOR);
		setLayout(new BorderLayout());
		addMouseListener(mouseListener);

		label.setForeground(FG_COLOR);
		label.setFont(new Font("Sans", Font.BOLD, 90));
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setText(name);

		try {
			bgImg = ImageIO.read(new File(imgPath));
		} catch (IOException ex) {
			System.err.println("[error] cannot read image path '" + imgPath + "'");
			add(label, BorderLayout.CENTER);
		}
	}

	public void setAction(Runnable action) {this.action = action;}
	public void enableAction() {actionEnabled = true;}
	public void disableAction() {actionEnabled = false;}

	@Override
	public void paint(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		// That's for the label
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(gg);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gg = (Graphics2D) g;

		int w = getWidth();
		int h = getHeight();

		if (bgImg != null) {
			int imgW = bgImg.getWidth();
			int imgH = bgImg.getHeight();

			if ((float)w/h < (float)imgW/imgH) {
				int tw = h * imgW/ imgH;
				int th = h;
				gg.drawImage(bgImg, (w-tw)/2, 0, tw, th, null);
			} else {
				int tw = w;
				int th = w * imgH / imgW;
				gg.drawImage(bgImg, 0, (h-th)/2, tw, th, null);
			}
		}

		int t = borderThickness;
		gg.setColor(BORDER_COLOR);
		gg.fillRect(0, 0, t, h-1);
		gg.fillRect(0, 0, w-1, t);
		gg.fillRect(0, h-1-t, w-1, t);
		gg.fillRect(w-1-t, 0, t, h-1);
	}

	public final MouseListener mouseListener = new MouseAdapter() {
		@Override public void mouseEntered(MouseEvent e) {
			if (actionEnabled) {
				tweenManager.killTarget(borderThickness);
				Tween.to(ThePanel.this, Accessor.BORDER_THICKNESS, 0.4f)
					.target(10)
					.start(tweenManager);
			}
		}

		@Override public void mouseExited(MouseEvent e) {
			tweenManager.killTarget(borderThickness);
			Tween.to(ThePanel.this, Accessor.BORDER_THICKNESS, 0.4f)
				.target(2)
				.start(tweenManager);
		}

		@Override public void mouseReleased(MouseEvent e) {
			if (action != null && actionEnabled) action.run();
		}
	};

	// -------------------------------------------------------------------------
	// Tween Accessor
	// -------------------------------------------------------------------------

	public static class Accessor extends SLAnimator.JComponentAccessor {
		public static final int BORDER_THICKNESS = 100;

		@Override
		public int getValues(JComponent target, int tweenType, float[] returnValues) {
			ThePanel tp = (ThePanel) target;

			int ret = super.getValues(target, tweenType, returnValues);
			if (ret >= 0) return ret;

			switch (tweenType) {
				case BORDER_THICKNESS: returnValues[0] = tp.borderThickness; return 1;
				default: return -1;
			}
		}

		@Override
		public void setValues(JComponent target, int tweenType, float[] newValues) {
			ThePanel tp = (ThePanel) target;

			super.setValues(target, tweenType, newValues);

			switch (tweenType) {
				case BORDER_THICKNESS:
					tp.borderThickness = Math.round(newValues[0]);
					tp.repaint();
					break;
			}
		}
	}
}
