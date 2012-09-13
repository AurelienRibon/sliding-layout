package aurelienribon.slidinglayout;

import aurelienribon.tweenengine.TweenManager;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLayeredPane;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SLPanel extends JLayeredPane {
	private TweenManager tweenManager;
	SLConfig currentCfg;

	public SLPanel() {
		addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {
				if (currentCfg != null) initialize(currentCfg);
			}
		});
	}

	public void setTweenManager(TweenManager tweenManager) {
		this.tweenManager = tweenManager;
	}

	public void initialize(SLConfig cfg) {
		currentCfg = cfg;
		cfg.placeAndRoute();

		removeAll();
		for (Component c : cfg.getCmps()) {
			SLConfig.Tile t = cfg.getTile(c);
			c.setBounds(t.x, t.y, t.w, t.h);
			add(c, new Integer(1));
		}
	}

	public SLTimeline createTimeline() {
		return new SLTimeline(this, tweenManager);
	}
}
