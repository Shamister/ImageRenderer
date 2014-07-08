import comp102.UI;
import comp102.UIButtonListener;

/** ImageRendererGUI */
public class ImageRendererGUI implements UIButtonListener {
	private ImageRenderer ir = new ImageRenderer();

	public ImageRendererGUI() {
		UI.initialise();
		UI.addButton("Render .ppm Image", this);
		UI.addButton("Render Animated Image", this);
		UI.addButton("Tools", this);
	}

	public void buttonPerformed(String b) {
		UI.clearText();
		UI.clearGraphics();
		if (b.equals("Render .ppm Image")) {
			ir.renderImage();
		} else if (b.equals("Render Animated Image")) {
			ir.renderAnimatedImage();
		} else if (b.equals("Tools")) {
			ir.tools();
		}
	}

	public static void main(String[] arguments) {
		new ImageRendererGUI();
	}
}
