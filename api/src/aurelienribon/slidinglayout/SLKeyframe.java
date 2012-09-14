package aurelienribon.slidinglayout;

import aurelienribon.slidinglayout.SLConfig.Tile;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A keyframe wraps a {@link SLConfig layout configuration} together with
 * various parameters, such as the ending and starting sides of new and old
 * components, and the optional delays that can be applied to the components
 * to create some nice effects.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SLKeyframe {
	private final SLConfig cfg;
	private final float duration;
	private final Map<SLSide, List<Component>> startSides = new EnumMap<SLSide, List<Component>>(SLSide.class);
	private final Map<SLSide, List<Component>> endSides = new EnumMap<SLSide, List<Component>>(SLSide.class);
	private final Map<Component, Tile> startCmps = new HashMap<Component, Tile>();
	private final Map<Component, Tile> endCmps = new HashMap<Component, Tile>();
	private final Map<Component, Float> delays = new HashMap<Component, Float>();
	private Callback callback;

	/**
	 * The duration parameter controls the duration of the transition of a
	 * component from its current place to its target place in the keyframe
	 * layout configuration. It doesn't account for any optional delay you
	 * may add to it.
	 */
	public SLKeyframe(SLConfig cfg, float duration) {
		this.cfg = cfg;
		this.duration = duration;

		for (SLSide s : SLSide.values()) {
			startSides.put(s, new ArrayList<Component>());
			endSides.put(s, new ArrayList<Component>());
		}
	}

	/**
	 * A simple callback, yeah.
	 */
	public static interface Callback {
		public void done();
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Sets the side of the screen from where the given new components will
	 * slide from. A new component is a component that is present in this
	 * layout configuration but not on the previous one.
	 */
	public SLKeyframe setStartSide(SLSide side, Component... cmps) {
		startSides.get(side).addAll(Arrays.asList(cmps));
		return this;
	}

	/**
	 * Sets the side of the screen from where the given old components will
	 * slide to. An old component is a component that is not present in this
	 * layout configuration but was on the previous one.
	 */
	public SLKeyframe setEndSide(SLSide side, Component... cmps) {
		endSides.get(side).addAll(Arrays.asList(cmps));
		return this;
	}

	/**
	 * Sets the delay used by the given components before the transition from
	 * their current place to their target one will fire.
	 */
	public SLKeyframe setDelay(float delay, Component... cmps) {
		for (Component c : cmps) delays.put(c, delay);
		return this;
	}

	/**
	 * Sets the delay used by the given components before the transition from
	 * their current place to their target one will fire. The delay is
	 * increased for each component in the list.
	 */
	public SLKeyframe setDelayIncr(float delay, Component... cmps) {
		float d = 0;
		for (Component c : cmps) delays.put(c, d += delay);
		return this;
	}

	/**
	 * Sets a callback that will be called at the end of the transition to this
	 * keyframe.s
	 */
	public SLKeyframe setCallback(Callback callback) {
		this.callback = callback;
		return this;
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	void initialize(SLKeyframe prevKf) {
		cfg.placeAndRoute();

		for (Component c : prevKf.cfg.getCmps()) startCmps.put(c, prevKf.cfg.getTile(c).clone());
		for (Component c : cfg.getCmps()) endCmps.put(c, cfg.getTile(c).clone());

		List<Component> addedCmps = new ArrayList<Component>(cfg.getCmps());
		addedCmps.removeAll(prevKf.cfg.getCmps());
		List<Tile> addedTiles = new ArrayList<Tile>();
		for (Component c : addedCmps) addedTiles.add(cfg.getTile(c).clone());
		for (int i=0; i<addedCmps.size(); i++) startCmps.put(addedCmps.get(i), addedTiles.get(i));

		List<Component> removedCmps = new ArrayList<Component>(prevKf.cfg.getCmps());
		removedCmps.removeAll(cfg.getCmps());
		List<Tile> removedTiles = new ArrayList<Tile>();
		for (Component c : removedCmps) removedTiles.add(prevKf.cfg.getTile(c).clone());
		for (int i=0; i<removedCmps.size(); i++) endCmps.put(removedCmps.get(i), removedTiles.get(i));

		for (SLSide s : SLSide.values()) {
			hideTiles(getTiles(startSides.get(s), startCmps), s);
			hideTiles(getTiles(endSides.get(s), endCmps), s);
		}

		for (Component c : startCmps.keySet()) {
			Tile t = startCmps.get(c);
			c.setBounds(t.x, t.y, t.w, t.h);
		}

		cfg.getPanel().removeAll();
		for (Component c : endCmps.keySet()) {
			cfg.getPanel().add(c, new Integer(1));
		}
	}

	Set<Component> getEndCmps() {
		return endCmps.keySet();
	}

	Tile getEndTile(Component cmp) {
		return endCmps.get(cmp);
	}

	float getDelay(Component cmp) {
		return delays.containsKey(cmp) ? delays.get(cmp) : 0;
	}

	Callback getCallback() {
		return callback;
	}

	float getDuration() {
		return duration;
	}

	SLConfig getCfg() {
		return cfg;
	}

	// -------------------------------------------------------------------------
	// Private API
	// -------------------------------------------------------------------------

	private List<Tile> getTiles(List<Component> cmps, Map<Component, Tile> cmpsMap) {
		List<Tile> tiles = new ArrayList<Tile>();
		for (Component c : cmps) tiles.add(cmpsMap.get(c));
		return tiles;
	}

	private void hideTiles(List<Tile> tiles, SLSide side) {
		if (tiles.isEmpty()) return;

		int w = cfg.getPanel().getWidth();
		int h = cfg.getPanel().getHeight();

		switch (side) {
			case TOP:
				int maxY = tiles.get(0).y + tiles.get(0).h;
				for (Tile t : tiles) maxY = Math.max(maxY, t.y + t.h);
				for (Tile t : tiles) t.y -= maxY;
				break;

			case BOTTOM:
				int minY = tiles.get(0).y;
				for (Tile t : tiles) minY = Math.min(minY, t.y);
				for (Tile t : tiles) t.y += h - minY;
				break;

			case LEFT:
				int maxX = tiles.get(0).x + tiles.get(0).w;
				for (Tile t : tiles) maxX = Math.max(maxX, t.x + t.w);
				for (Tile t : tiles) t.x -= maxX;
				break;

			case RIGHT:
				int minX = tiles.get(0).x;
				for (Tile t : tiles) minX = Math.min(minX, t.x);
				for (Tile t : tiles) t.x += w - minX;
				break;
		}
	}
}
