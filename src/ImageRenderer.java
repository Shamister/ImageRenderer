/* Code for COMP102 Assignment 5
 * Name:
 * Usercode:
 * ID:
 */

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import comp102.UI;
import comp102.UIFileChooser;

/**
 * Renders plain ppm images onto the graphics panel ppm images are the simplest
 * possible colour image format.
 */

public class ImageRenderer {
	public static final int left = 20; // left edge of the image
	public static final int top = 20; // top edge of the image
	public static final int pixelSize = 2;

	public boolean showingImage;

	/**
	 * Renders a ppm image file. Asks for the name of the file, then renders the
	 * image at position (left, top). Each pixel of the image is rendered by a
	 * square of size pixelSize Assumes that - the colour depth is 255, - there
	 * is just one image in the file (not "animated"), and - there are no
	 * comments in the file. The first four tokens are P3, number of columns,
	 * number of rows, 255 The remaining tokens are the pixel values (red,
	 * green, blue for each pixel)
	 */
	public void renderImage() {
		// YOUR CODE HERE
		try {
			UI.clearGraphics();
			showingImage = false;
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			skipWhiteSpace(image);
			skipComment(image);
			String magic = image.next();
			skipWhiteSpace(image);
			skipComment(image);
			skipNotInt(image);
			int width = image.nextInt();
			skipWhiteSpace(image);
			skipComment(image);
			skipNotInt(image);
			int height = image.nextInt();
			skipWhiteSpace(image);
			skipComment(image);
			int max_color = image.nextInt();
			if (magic.equals("P3")) {
				while (image.hasNextInt()) {
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							while (image.hasNextInt()) {
								int red = image.nextInt();
								int green = image.nextInt();
								int blue = image.nextInt();
								UI.setColor(new Color(red, green, blue));
								double x1 = left + x * 2;
								double y1 = top + y * 2;
								UI.fillRect(x1, y1, 2, 2, false);
								break;
							}
						}
					}
				}
				image.close();
			} else {
				image.close();
				UI.println("Sorry, error occured when reading the file");
			}
		}

		catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
		UI.repaintGraphics();
	}

	/**
	 * Renders a ppm image file, possibly animated (multiple images in the file)
	 * Asks for the name of the file, then renders the image at position (left,
	 * top). Each pixel of the image is rendered by a square of size pixelSize
	 * Renders each image in the file in turn with 200 mSec delay. Repeats the
	 * sequence 3 times. Ignores comments (starting with # and occuring after
	 * the 1st, 2nd, or 3rd token) The colour depth (max colour value) may be
	 * different from 255 (scales the colour values appropriately)
	 */
	public void renderAnimatedImage() {
		// YOUR CODE HERE
		try {
			showingImage = false;
			UI.clearGraphics();
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			showImage(image);
			showingImage = true;
			while (showingImage) {
				image = new Scanner(new File(fileName));
				showImage(image);
			}
		} catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
	}

	public void showImage(Scanner image) {
		while (image.hasNext()) {
			UI.clearGraphics(false);
			skipWhiteSpace(image);
			skipComment(image);
			String magic = null;
			skipWhiteSpace(image);
			skipComment(image);
			int width = 0;
			skipWhiteSpace(image);
			skipComment(image);
			int height = 0;
			skipWhiteSpace(image);
			skipComment(image);
			int scale = 0;
			skipWhiteSpace(image);
			skipComment(image);
			int depth = 0;
			// comment always starts with '#' and last to the end of the line
			while (image.hasNext()) {
				String read = image.next();
				if (read.contains("#")) {
					image.nextLine();
				} else if (magic == null) {
					magic = read;
					if (!magic.equals("P3")) {
						image.close();
						UI.println("File is not .ppm format");
					}
				} else if ((magic != null) && (width == 0)) {
					width = Integer.parseInt(read);
				} else if ((width != 0) && (height == 0)) {
					height = Integer.parseInt(read);
				} else if ((height != 0) && (depth == 0)) {
					depth = Integer.parseInt(read);
					scale = 255 / depth;
					break;
				}
			}

			if (image.hasNextInt()) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (image.hasNextInt()) {
							int red = image.nextInt() * scale;
							int green = image.nextInt() * scale;
							int blue = image.nextInt() * scale;
							UI.setColor(new Color(red, green, blue));
							double x1 = left + x * 2;
							double y1 = top + y * 2;
							UI.fillRect(x1, y1, 2, 2, false);
						}
					}
				}
			}

			try {
				UI.repaintGraphics();
				Thread.sleep(200);
			}

			catch (InterruptedException ie) {
			}
		}
	}

	public void renderImageChallenge() {
		try {
			UI.clearGraphics();
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			UI.clearGraphics();
			String magic = null;
			// comment always starts with '#' and last to the end of the line
			while (image.hasNext()) {
				String read = image.next();
				if (read.contains("#")) {
					image.nextLine();
				} else if (magic == null) {
					magic = read;
					if (magic.equals("P1")) {
						pbm(image);
					} else if (magic.equals("P2")) {
						pgm(image);
					} else {
						UI.println("File is not either .pbm or .pgm format");
					}
					break;
				}
			}
			image.close();
		} catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
	}

	public void pbm(Scanner image) {
		int width = 0;
		int height = 0;
		// comment always starts with '#' and last to the end of the line
		while (image.hasNext()) {
			String read = image.next();
			if (read.contains("#")) {
				image.nextLine();
			} else if (width == 0) {
				width = Integer.parseInt(read);
			} else if ((width != 0) && (height == 0)) {
				height = Integer.parseInt(read);
				break;
			}

		}

		if (image.hasNextDouble()) {
			String lcode = image.next();
			long code = Long.parseLong(lcode);
			int color = 255;
			if (lcode.length() > 1) { // for .pbm format written without space
				int length = lcode.length();
				int x = 0;
				int y = 0;
				for (int i = 0; i < length; i++) {
					int ccode = (int) (Long
							.parseLong(lcode.substring(i, i + 1)));
					if (ccode == 0) {
						color = 255;
					} else if (ccode == 1) {
						color = 0;
					}
					UI.setColor(new Color(color, color, color));
					double x1 = left + x * 2;
					double y1 = top + y * 2;
					UI.fillRect(x1, y1, 2, 2, false);
					if (x == (width - 1)) {
						x = 0;
						y++;
					} else {
						x++;
					}
				}

				while (image.hasNextDouble()) {
					lcode = image.next();
					length = lcode.length();
					for (int i = 0; i < length; i++) {
						int ccode = Integer.parseInt(lcode.substring(i, i + 1));
						if (ccode == 0) {
							color = 255;
						} else if (ccode == 1) {
							color = 0;
						}
						UI.setColor(new Color(color, color, color));
						double x1 = left + x * 2;
						double y1 = top + y * 2;
						UI.fillRect(x1, y1, 2, 2, false);
						if ((i == (length - 1)) && (image.hasNextDouble())) {
							lcode = image.next();
							length = lcode.length();
						}
						if (x == (width - 1)) {
							x = 0;
							y++;
						} else {
							x++;
						}
					}
				}
			}

			else { // .pbm format written with space
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (code == 0) {
							color = 255;
						} else if (code == 1) {
							color = 0;
						}
						UI.setColor(new Color(color, color, color));
						double x1 = left + x * 2;
						double y1 = top + y * 2;
						UI.fillRect(x1, y1, 2, 2, false);
						if (image.hasNextInt()) {
							code = image.nextInt();
						}
					}
				}
			}
			UI.repaintGraphics();
		}
	}

	public void pgm(Scanner image) {
		int width = 0;
		int height = 0;
		int scale = 0;
		int depth = 0;

		// comment always starts with '#' and last to the end of the line
		while (image.hasNext()) {
			String read = image.next();
			if (read.contains("#")) {
				image.nextLine();
			} else if (width == 0) {
				width = Integer.parseInt(read);
			} else if ((width != 0) && (height == 0)) {
				height = Integer.parseInt(read);
			} else if ((height != 0) && (depth == 0)) {
				depth = Integer.parseInt(read);
				scale = 255 / depth;
				break;
			}
		}

		while (image.hasNextInt()) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					while (image.hasNextInt()) {
						int color = image.nextInt() * scale;
						UI.setColor(new Color(color, color, color));
						double x1 = left + x * 2;
						double y1 = top + y * 2;
						UI.fillRect(x1, y1, 2, 2, false);
						break;
					}
				}
			}
		}
		UI.repaintGraphics();
	}

	public void tools() {
		UI.println("1. Compress *.pbm (*.pbmc file)");
		UI.println("2. Open *.pbmc file");
		UI.println("3. Open *.pbm or *.pgm file");
		UI.println("4. .ppm to .pbm");
		int choice = UI.askInt("Select choice : ");
		if (choice == 1) {
			compress_pbm();
		} else if (choice == 2) {
			pbmc();
		} else if (choice == 3) {
			renderImageChallenge();
		} else if (choice == 4) {
			ppmtopbm();
		}
	}

	public void compress_pbm() {

		try {
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			String name = fileName.substring(0, fileName.length() - 4)
					+ ".pbmc";
			PrintStream out = new PrintStream(name);
			String magic = null;
			int width = 0;
			int height = 0;

			// comment always starts with '#' and last to the end of the line
			while (image.hasNext()) {
				String read = image.next();
				if (read.contains("#")) {
					image.nextLine();
				} else if (magic == null) {
					magic = read;
					if (!magic.equals("P1")) {
						image.close();
						UI.println("File is not .pbm format");
					}
				} else if ((magic != null) && (width == 0)) {
					width = Integer.parseInt(read);
				} else if ((width != 0) && (height == 0)) {
					height = Integer.parseInt(read);
					break;
				}
			}

			if (image.hasNextDouble()) {
				int a = 0;
				int b = 0;
				out.println(magic);
				out.println(width + " " + height);
				String lcode = image.next();
				int code = Integer.parseInt(lcode);
				if (lcode.length() > 1) { // for .pbm format written without
											// space
					int length = lcode.length();
					int c = 0;

					for (int i = 0; i < length; i++) {
						int ccode = Integer.parseInt(lcode.substring(i, i + 1));
						c++;
						if ((ccode == 0) && (b > 0)) {
							out.print(b + "b ");
							a = 1;
							b = 0;
						} else if ((ccode == 1) && (a > 0)) {
							out.print(a + "a ");
							b = 1;
							a = 0;
						} else if (ccode == 0) {
							a++;
							if (c == (width * height)) {
								if (a > 0) {
									out.print(a + "a");
								} else if (b > 0) {
									out.print(b + "b");
								}
							}
						} else if (ccode == 1) {
							b++;
							if (c == (width * height)) {
								if (a > 0) {
									out.print(a + "a");
								} else if (b > 0) {
									out.print(b + "b");
								}
							}
						}
						if ((i == (length - 1)) && (image.hasNextDouble())) {
							lcode = image.next();
							length = lcode.length();
						}
					}

					while (lcode != null) {
						if (c == (width * height)) {
							break;
						}
						for (int i = 0; i < length; i++) {
							int ccode = Integer.parseInt(lcode.substring(i,
									i + 1));
							c++;
							if ((ccode == 0) && (b > 0)) {
								out.print(b + "b ");
								a = 1;
								b = 0;
							} else if ((ccode == 1) && (a > 0)) {
								out.print(a + "a ");
								b = 1;
								a = 0;
							} else if (ccode == 0) {
								a++;
								if (c == (width * height)) {
									if (a > 0) {
										out.print(a + "a");
									} else if (b > 0) {
										out.print(b + "b");
									}
								}
							} else if (ccode == 1) {
								b++;
								if (c == (width * height)) {
									if (a > 0) {
										out.print(a + "a");
									} else if (b > 0) {
										out.print(b + "b");
									}
								}
							}
							if ((i == (length - 1)) && (image.hasNextDouble())) {
								lcode = image.next();
								length = lcode.length();
							}
						}
					}
				}

				else { // for .pbm format written with space
					for (int c = 0; c < width * height; c++) {
						if ((code == 0) && (b > 0)) {
							out.print(b + "b ");
							a = 1;
							b = 0;
						} else if ((code == 1) && (a > 0)) {
							out.print(a + "a ");
							b = 1;
							a = 0;
						} else if (code == 0) {
							a++;
							if (c == (width * height) - 1) {
								if (a > 0) {
									out.print(a + "a");
								} else if (b > 0) {
									out.print(b + "b");
								}
							}
						} else if (code == 1) {
							b++;
							if (c == (width * height) - 1) {
								if (a > 0) {
									out.print(a + "a");
								} else if (b > 0) {
									out.print(b + "b");
								}
							}
						}
						if (image.hasNextInt()) {
							code = image.nextInt();
						}
					}
				}
				UI.println("File " + fileName
						+ " has been compressed successfully!");
				UI.println(name + " file has been created");
				image.close();
			}
		}

		catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
	}

	public void pbmc() {
		try {
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			String magic = null;
			double width = 0;
			double height = 0;
			// comment always starts with '#' and last to the end of the line
			while (image.hasNext()) {
				String read = image.next();
				if (read.contains("#")) {
					image.nextLine();
				} else if (magic == null) {
					magic = read;
					if (!magic.equals("P1")) {
						image.close();
						UI.println("File is not .pbmc format");
					}
				} else if (width == 0) {
					width = Double.parseDouble(read);
				} else if ((width != 0) && (height == 0)) {
					height = Double.parseDouble(read);
					break;
				}

			}

			ArrayList<Integer> clist = new ArrayList<Integer>();

			while (image.hasNext()) {
				String data = image.next();
				int total = Integer.parseInt(data.substring(0,
						data.length() - 1));
				String code = data.substring(data.length() - 1, data.length());
				if (code.equals("a")) {
					for (int c = 0; c < total; c++) {
						clist.add(0);
					}
				} else if (code.equals("b")) {
					for (int c = 0; c < total; c++) {
						clist.add(1);
					}
				}
			}

			int start = 1;

			for (int y = 1; y <= height; y++) {
				for (int x = start; x <= width * y; x++) {
					int code = clist.get(start - 1);
					int color = 255;
					if (code == 0) {
						color = 255;
					} else if (code == 1) {
						color = 0;
					}
					UI.setColor(new Color(color, color, color));
					double x1 = left + ((x % width) - 1) * 2;
					double y1 = top + (y - 1) * 2;
					UI.fillRect(x1, y1, 2, 2, false);
					start++;
				}
			}
			UI.repaintGraphics();
		}

		catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
	}

	public void ppmtopbm() {

		try {
			String fileName = UIFileChooser.open("Choose a file");
			Scanner image = new Scanner(new File(fileName));
			String name = fileName.substring(0, fileName.length() - 4) + ".pbm";
			PrintStream out = new PrintStream(name);
			UI.clearGraphics();
			String magic = null;
			int width = 0;
			int height = 0;
			int depth = 0;
			int scale = 255;

			// comment always starts with '#' and last to the end of the line
			while (image.hasNext()) {
				String read = image.next();
				if (read.contains("#")) {
					image.nextLine();
				} else if (magic == null) {
					magic = read;
					if (!magic.equals("P3")) {
						UI.println("File is not .ppm format");
					}
				} else if ((magic != null) && (width == 0)) {
					width = Integer.parseInt(read);
				} else if ((width != 0) && (height == 0)) {
					height = Integer.parseInt(read);
				} else if ((height != 0) && (depth == 0)) {
					depth = Integer.parseInt(read);
					scale = 255 / depth;
					break;
				}
			}

			while (image.hasNextInt()) {
				out.println("P1");
				out.println(width + " " + height);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (image.hasNextInt()) {
							int red = image.nextInt() * scale;
							int green = image.nextInt() * scale;
							int blue = image.nextInt() * scale;
							int color = (blackwhite(red, green, blue));
							out.print((color / 255) + " ");
							UI.setColor(new Color(1 - (float) (color / 255),
									1 - (float) (color / 255),
									1 - (float) (color / 255)));
							double x1 = left + x * 2;
							double y1 = top + y * 2;
							UI.fillRect(x1, y1, 2, 2, false);
							if ((x + 1) % 5 == 0) {
								out.println();
							}
						}
					}
				}
			}

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
				}
			}
			UI.repaintGraphics();
			UI.println("File " + name + " has been created successfully!");
			image.close();
		}

		catch (IOException e) {
			UI.println("Sorry, cannot create image from file");
		}
	}

	private final String nonUsed = "[\\s\\t\\n]";

	public void skipWhiteSpace(Scanner s) {
		while (s.hasNext(nonUsed)) {
			s.next(nonUsed);
		}
	}

	public void skipNotInt(Scanner s) {
		while (!s.hasNextInt()) {
			s.next();
		}
	}

	public void skipComment(Scanner s) {
		while (s.hasNext("#")) {
			String a = s.nextLine();
		}
	}

	public int blackwhite(int red, int green, int blue) {
		// thresholding using color formula
		double I = (red + green + blue) / 3; // intensity
		double Y = 0.299 * red + 0.587 * green + 0.114 * blue; // luminosity
		if (Y > 128) {
			return 0;
		} else {
			if (I > 160) {
				return 0;
			} else {
				return 255;
			}
		}
	}
}
