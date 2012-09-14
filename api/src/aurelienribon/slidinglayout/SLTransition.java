package aurelienribon.slidinglayout;

import aurelienribon.slidinglayout.SLConfig.Tile;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SLTransition {
	private final SLPanel panel;
	private final TweenManager tweenManager;
	private final List<SLKeyframe> keyframes = new ArrayList<SLKeyframe>();
	private int currentKeyframe;
	private Timeline timeline;

	public SLTransition(SLPanel panel, TweenManager tweenManager) {
		this.panel = panel;
		this.tweenManager = tweenManager;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public SLTransition push(SLKeyframe kf) {
		keyframes.add(kf);
		return this;
	}

	public SLTransition play() {
		currentKeyframe = 0;
		play(keyframes.get(0), new SLKeyframe(panel.currentCfg, 0));
		return this;
	}

	// -------------------------------------------------------------------------
	// Private API
	// -------------------------------------------------------------------------

	private void play(SLKeyframe kf, SLKeyframe previousKf) {
		panel.currentCfg = kf.getCfg();

		kf.initialize(previousKf);
		tween(kf);
	}

	private void tween(final SLKeyframe kf) {
		if (timeline != null) timeline.kill();

		timeline = Timeline.createParallel();

		for (Component c : kf.getEndCmps()) {
			Tile t = kf.getEndTile(c);

			int dx = c.getX() - t.x;
			int dy = c.getY() - t.y;
			int dw = c.getWidth() - t.w;
			int dh = c.getHeight() - t.h;
			boolean animXY = (dx != 0) || (dy != 0);
			boolean animWH = (dw != 0) || (dh != 0);
			float duration = kf.getDuration();

			if (animXY && animWH) {
				timeline.push(Tween.to(c, SLAnimator.JComponentAccessor.XYWH, duration)
					.target(t.x, t.y, t.w, t.h)
					.delay(kf.getDelay(c))
				);
			} else if (animXY) {
				timeline.push(Tween.to(c, SLAnimator.JComponentAccessor.XY, duration)
					.target(t.x, t.y)
					.delay(kf.getDelay(c))
				);
			} else if (animWH) {
				timeline.push(Tween.to(c, SLAnimator.JComponentAccessor.WH, duration)
					.target(t.w, t.h)
					.delay(kf.getDelay(c))
				);
			}
		}

		timeline.setCallback(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				if (kf.getCallback() != null) kf.getCallback().done();
				if (currentKeyframe < keyframes.size()-1) {
					currentKeyframe++;
					play(keyframes.get(currentKeyframe), keyframes.get(currentKeyframe-1));
				}
			}
		});

		timeline.start(tweenManager);
	}
}
