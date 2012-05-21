package com.me.whiteboard;

public class Display {

	public final static float bmpScale = 2;
	private final static int frameLength = 10;
	private final static int frameInteveral = 10;
	private final static float MAX_SCALEFACTOR = 15;
	private final static float MIN_SCALEFACTOR = 1;
	private final static float alpha = 15;
	private final static float beta = 1;
	private final static float gamma = (float) 0.8;
	private final static float MAX_SCALEFACTOR_REAL = (float) 14.3928;
	private final static float MIN_SCALEFACTOR_REAL = 1;

	public static int screen_width;
	public static int screen_height;
	public static int bmp_width;
	public static int bmp_height;
	private static float screen_pos_x;
	private static float screen_pos_y;
	private static float scaleFactor_real;
	private static float scaleFactor;
	private static float screen_pos_x_Bmp;
	private static float screen_pos_y_Bmp;
	private static float scaleFactor_Bmp;
	public static int previousPointCount = 0;
	// private static float x_mean;
	// private static float y_mean;
	private static float sumOfLength;
	// private static float x_mean_absolute;
	// private static float y_mean_absolute;
	private static float x_mean_absolute;
	private static float y_mean_absolute;

	public Display(int width, int height) {
		screen_width = width;
		screen_height = height;
		scaleFactor_real = 1;
		scaleFactor = scaleFactorRealToDisplay(scaleFactor_real);
		screen_pos_x = 0;
		screen_pos_y = 0;

		if (((float) screen_width) / screen_height > 4.0 / 3.0) {
			screen_width = screen_height * 4 / 3;
		} else {
			screen_height = screen_width * 3 / 4;
		}

		bmp_width = (int) (screen_width * bmpScale);
		bmp_height = (int) (screen_height * bmpScale);

		reSize();
	}

	private static void reset(float x_mean, float y_mean, float sumOfLength) {
		// Display.x_mean = x_mean;
		// Display.y_mean = y_mean;
		Display.x_mean_absolute = x_RelativeToAbsolute(x_mean);
		Display.y_mean_absolute = y_RelativeToAbsolute(y_mean);
		// Display.x_mean_bmpPos = x_ScreenPosToBmpPos(x_mean);
		// Display.y_mean_bmpPos = y_ScreenPosToBmpPos(y_mean);
		Display.sumOfLength = sumOfLength;
	}
	
	private static float scaleFactorRealToDisplay(float x) {
		return (float) (alpha * Math.atan((x-beta)/alpha)+beta+gamma*(Math.exp(-x)-Math.exp(-1)));
	}

	public static void update(int pointCount, float x_mean, float y_mean,
			float sumOfLength) {
		if (pointCount != previousPointCount) {
			reset(x_mean, y_mean, sumOfLength);
			return;
		}

		// screen_pos_x += (Display.x_mean - x_mean) / scaleFactor;
		// screen_pos_y += (Display.y_mean - y_mean) / scaleFactor;
		
		scaleFactor_real *= sumOfLength / Display.sumOfLength;
		scaleFactor = scaleFactorRealToDisplay(scaleFactor_real);
		screen_pos_x += x_mean_absolute - x_RelativeToAbsolute(x_mean);
		screen_pos_y += y_mean_absolute - y_RelativeToAbsolute(y_mean);

		// android.util.Log.v("scaleFactor", Float.toString(scaleFactor));

		reset(x_mean, y_mean, sumOfLength);
	}

	public static void animate(MainActivity activity) {
		if (scaleFactor < MIN_SCALEFACTOR || scaleFactor > MAX_SCALEFACTOR
				|| screen_pos_x < 0
				|| screen_pos_x > screen_width - screen_width / scaleFactor
				|| screen_pos_y < 0
				|| screen_pos_y > screen_height - screen_height / scaleFactor) {

			float x_mean = x_AbsoluteToRelative(x_mean_absolute);
			float y_mean = y_AbsoluteToRelative(y_mean_absolute);
			float scaleFactorAnimation[] = new float[frameLength];
			float screen_pos_x_animation[] = new float[frameLength];
			float screen_pos_y_animation[] = new float[frameLength];

			if (scaleFactor < MIN_SCALEFACTOR) {
				scaleFactor_real = MIN_SCALEFACTOR_REAL;
				for (int i = 0; i < frameLength; i++) {
					scaleFactorAnimation[i] = scaleFactor
							+ (MIN_SCALEFACTOR - scaleFactor)
							/ (frameLength - 1) * i;
				}
			} else if (scaleFactor > MAX_SCALEFACTOR) {
				scaleFactor_real = MAX_SCALEFACTOR_REAL;
				for (int i = 0; i < frameLength; i++) {
					scaleFactorAnimation[i] = scaleFactor
							- (scaleFactor - MAX_SCALEFACTOR)
							/ (frameLength - 1) * i;
				}
			} else {
				for (int i = 0; i < frameLength; i++) {
					scaleFactorAnimation[i] = scaleFactor;
				}
			}

			for (int i = 0; i < frameLength; i++) {
				scaleFactor = scaleFactorAnimation[i];
				screen_pos_x += x_mean_absolute - x_RelativeToAbsolute(x_mean);
				screen_pos_y += y_mean_absolute - y_RelativeToAbsolute(y_mean);
				screen_pos_x_animation[i] = screen_pos_x;
				screen_pos_y_animation[i] = screen_pos_y;
				x_mean_absolute = x_RelativeToAbsolute(x_mean);
				y_mean_absolute = y_RelativeToAbsolute(y_mean);
			}

			if (screen_pos_x < 0) {
				for (int i = 0; i < frameLength; i++) {
					screen_pos_x_animation[i] -= screen_pos_x
							/ (frameLength - 1) * i;
				}
			} else if (screen_pos_x > screen_width - screen_width / scaleFactor) {
				for (int i = 0; i < frameLength; i++) {
					screen_pos_x_animation[i] -= (screen_pos_x - screen_width + screen_width
							/ scaleFactor)
							/ (frameLength - 1) * i;
				}
			}

			if (screen_pos_y < 0) {
				for (int i = 0; i < frameLength; i++) {
					screen_pos_y_animation[i] -= screen_pos_y
							/ (frameLength - 1) * i;
				}
			} else if (screen_pos_y > screen_height - screen_height
					/ scaleFactor) {
				for (int i = 0; i < frameLength; i++) {
					screen_pos_y_animation[i] -= (screen_pos_y - screen_height + screen_height
							/ scaleFactor)
							/ (frameLength - 1) * i;
				}
			}

			for (int i = 0; i < frameLength; i++) {
				scaleFactor = scaleFactorAnimation[i];
				screen_pos_x = screen_pos_x_animation[i];
				screen_pos_y = screen_pos_y_animation[i];
				activity.FlushCanvas();
				try {
					Thread.sleep(frameInteveral);
				} catch (Exception e) {
				}
			}
		}
	}

	public static void reSize() {
		screen_pos_x_Bmp = screen_pos_x - (bmpScale - 1) / 2 * screen_width
				/ scaleFactor;
		screen_pos_y_Bmp = screen_pos_y - (bmpScale - 1) / 2 * screen_height
				/ scaleFactor;
		scaleFactor_Bmp = scaleFactor;
	}
	
	public static float width_AbsoluteToRelative(float width_absolute) {
		return width_absolute * scaleFactor;
	}

	public static float x_ScreenPosToBmpPos(float x_screenPos) {
		return (x_RelativeToAbsolute(x_screenPos) - x_BmpPosToAbsolute(0))
				* scaleFactor_Bmp;
	}

	public static float y_ScreenPosToBmpPos(float y_screenPos) {
		return (y_RelativeToAbsolute(y_screenPos) - y_BmpPosToAbsolute(0))
				* scaleFactor_Bmp;
	}

	public static float x_BmpPosToScreenPos(float x_BmpPos) {
		return (x_BmpPosToAbsolute(x_BmpPos) - screen_pos_x) * scaleFactor;
	}

	public static float y_BmpPosToScreenPos(float y_BmpPos) {
		return (y_BmpPosToAbsolute(y_BmpPos) - screen_pos_y) * scaleFactor;
	}

	public static float x_AbsoluteToRelative(float x_absolute) {
		return (x_absolute - screen_pos_x) * scaleFactor;
	}

	public static float y_AbsoluteToRelative(float y_absolute) {
		return (y_absolute - screen_pos_y) * scaleFactor;
	}

	public static float x_RelativeToAbsolute(float x_relative) {
		return screen_pos_x + x_relative / scaleFactor;
	}

	public static float y_RelativeToAbsolute(float y_relative) {
		return screen_pos_y + y_relative / scaleFactor;
	}

	public static float x_AbsoluteToBmpPos(float x_absolute) {
		return (x_absolute - screen_pos_x_Bmp) * scaleFactor_Bmp;
	}

	public static float y_AbsoluteToBmpPos(float y_absolute) {
		return (y_absolute - screen_pos_y_Bmp) * scaleFactor_Bmp;
	}

	private static float x_BmpPosToAbsolute(float x_relative_Bmp) {
		return screen_pos_x_Bmp + x_relative_Bmp / scaleFactor_Bmp;
	}

	private static float y_BmpPosToAbsolute(float y_relative_Bmp) {
		return screen_pos_y_Bmp + y_relative_Bmp / scaleFactor_Bmp;
	}
}
