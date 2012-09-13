package aurelienribon.slidinglayout.demo;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TheFrame extends JFrame {
	private final SLPanel panel = new SLPanel();
	private final ThePanel p1 = new ThePanel("1", "data/img1.jpg");
	private final ThePanel p2 = new ThePanel("2", "data/img2.jpg");
	private final ThePanel p3 = new ThePanel("3", "data/img3.jpg");
	private final ThePanel p4 = new ThePanel("4", "data/img4.jpg");
	private final ThePanel p5 = new ThePanel("5", "data/img5.jpg");
	private final SLConfig mainCfg, p1Cfg, p2Cfg, p3Cfg, p4Cfg, p5Cfg;

	public TheFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Sliding Panels Test");
		getContentPane().setBackground(Color.WHITE);
		getContentPane().add(panel, BorderLayout.CENTER);

		p1.setAction(p1Action);
		p2.setAction(p2Action);
		p3.setAction(p3Action);
		p4.setAction(p4Action);
		p5.setAction(p5Action);

		mainCfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).col(250).col(1f).col(2f)
			.beginGrid(0, 0)
				.row(2f).row(1f).col(1f)
				.tile(0, 0, p1)
				.tile(1, 0, p2)
			.endGrid()
			.beginGrid(0, 1)
				.row(1f).row(2f).col(1f)
				.tile(0, 0, p3)
				.tile(1, 0, p4)
			.endGrid()
			.tile(0, 2, p5);

		p1Cfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).col(250).col(1f).col(2f)
			.tile(0, 0, p1)
			.beginGrid(0, 1)
				.row(1f).row(2f).col(1f)
				.tile(0, 0, p3)
				.tile(1, 0, p4)
			.endGrid()
			.tile(0, 2, p5);

		p2Cfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).col(2f).col(1f)
			.beginGrid(0, 0)
				.row(2f).row(1f).col(1f)
				.beginGrid(0, 0)
					.row(1f).col(250).col(1f)
					.tile(0, 0, p1)
					.tile(0, 1, p3)
				.endGrid()
				.tile(1, 0, p2)
			.endGrid()
			.tile(0, 1, p5);

		p3Cfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).col(2f).col(1f)
			.tile(0, 0, p3)
			.tile(0, 1, p5);

		p4Cfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).row(1f).col(1f).col(1f)
			.tile(0, 0, p1)
			.tile(1, 0, p2)
			.tile(0, 1, p3)
			.tile(1, 1, p4);

		p5Cfg = new SLConfig(panel)
			.gap(10, 10)
			.row(1f).col(1f)
			.tile(0, 0, p5);

		panel.setTweenManager(SLAnimator.createTweenManager());
		panel.initialize(mainCfg);
	}

	private void disableActions() {
		p1.disableAction();
		p2.disableAction();
		p3.disableAction();
		p4.disableAction();
		p5.disableAction();
	}

	private void enableActions() {
		p1.enableAction();
		p2.enableAction();
		p3.enableAction();
		p4.enableAction();
		p5.enableAction();
	}

	private final Runnable p1Action = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(p1Cfg, 0.6f)
				.setEndSide(SLSide.BOTTOM, p2)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p1.setAction(p1BackAction);
					p1.enableAction();
				}}))
			.play();
	}};

	private final Runnable p1BackAction = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(mainCfg, 0.6f)
				.setStartSide(SLSide.BOTTOM, p2)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p1.setAction(p1Action);
					enableActions();
				}}))
			.play();
	}};

	private final Runnable p2Action = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(p2Cfg, 0.6f)
				.setEndSide(SLSide.BOTTOM, p4)
				.setDelay(0.6f, p2)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p2.setAction(p2BackAction);
					p2.enableAction();
				}}))
			.play();
	}};

	private final Runnable p2BackAction = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(mainCfg, 0.6f)
				.setStartSide(SLSide.BOTTOM, p4)
				.setDelay(0.6f, p4)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p2.setAction(p2Action);
					enableActions();
				}}))
			.play();
	}};

	private final Runnable p3Action = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(p3Cfg, 0.8f)
				.setEndSide(SLSide.LEFT, p1, p2)
				.setEndSide(SLSide.BOTTOM, p4)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p3.setAction(p3BackAction);
					p3.enableAction();
				}}))
			.play();
	}};

	private final Runnable p3BackAction = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(mainCfg, 0.8f)
				.setStartSide(SLSide.LEFT, p1, p2)
				.setStartSide(SLSide.BOTTOM, p4)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p3.setAction(p3Action);
					enableActions();
				}}))
			.play();
	}};

	private final Runnable p4Action = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(p4Cfg, 0.6f)
				.setEndSide(SLSide.RIGHT, p5)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p4.setAction(p4BackAction);
					p4.enableAction();
				}}))
			.play();
	}};

	private final Runnable p4BackAction = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(mainCfg, 0.6f)
				.setStartSide(SLSide.RIGHT, p5)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p4.setAction(p4Action);
					enableActions();
				}}))
			.play();
	}};

	private final Runnable p5Action = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(p5Cfg, 0.8f)
				.setEndSide(SLSide.LEFT, p1, p2, p3, p4)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p5.setAction(p5BackAction);
					p5.enableAction();
				}}))
			.play();
	}};

	private final Runnable p5BackAction = new Runnable() {@Override public void run() {
		disableActions();

		panel.createTimeline()
			.push(new SLKeyframe(mainCfg, 0.8f)
				.setStartSide(SLSide.LEFT, p1, p2, p3, p4)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					p5.setAction(p5Action);
					enableActions();
				}}))
			.play();
	}};
}
