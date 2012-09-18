# SlidingLayout

This little library lets you very easily create **smooth transitions** between two layouts of components in a special panel.
The layouts are based on a grid, which can nest sub-grids and provides fixed/flexible columns and rows.

The library is **fully documented** (javadoc everywhere) to get you started quickly.

**Note:**
*The library uses the [Universal Tween Engine](http://www.aurelienribon.com/blog/projects/universal-tween-engine/)
under the hood, so you need to have this library in your classpath. It is compatible with the revision 6.3.3.*

## Examples

The following animated gif features an example of what can be done with the library.

![](http://www.aurelienribon.com/blog/wp-content/uploads/2012/09/sliding-layout-demo.gif)

There is also a [Youtube video](https://www.youtube.com/watch?v=R6rkcLAjTmA&feature=player_embedded) featuring the same demo with images.
The source code of these examples is available in the **demo folder** of this project.

## Help

Please use the [issue tracker](https://github.com/AurelienRibon/sliding-layout/issues) of this repository to report a bug.
Else, there is a [dedicated forum](http://www.aurelienribon.com/forum/viewforum.php?f=15) for you if you need some help 
to understand how it works or how to setup your project :)

## License

The library is distributed under the [Apache-2 License](http://www.apache.org/licenses/LICENSE-2.0.html).
Feel free to modify it, distribute it and include it in your projects as you want.

## Howto's

General setup:

```java
// First create the panel and give it a way to animate
// its transitions

SLPanel panel = new SLPanel();
panel.setTweenManager(SLAnimator.createTweenManager());

// Create your configurations

SLConfig config1 = ... ;
SLConfig config2 = ... ;

// Then set the panel to its initial configuration

panel.initialize(config1);

// Now, whenever you want, you can fire a transition

panel.createTimeline()
	.push(new SLKeyframe(config2, 0.6f)
		.setEndSide(SLSide.BOTTOM, child1, child2)
		.setStartSide(SLSide.UP, child3, child4))
	.play();
```
		
Creating a layout configuration is very simple (extracted from the demo):

```java
SLConfig mainCfg = new SLConfig(panel)
	.gap(10, 10)
	.row(1f).col(250).col(1f).col(2f)
	.beginGrid(0, 0)
		.row(2f).row(1f).col(1f)
		.place(0, 0, p1)
		.place(1, 0, p2)
	.endGrid()
	.beginGrid(0, 1)
		.row(1f).row(2f).col(1f)
		.place(0, 0, p3)
		.place(1, 0, p4)
	.endGrid()
	.place(0, 2, p5);
```