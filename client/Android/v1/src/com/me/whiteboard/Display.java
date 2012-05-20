package com.me.whiteboard;

public class Display {

	public static int screen_width;
	public static int screen_height;
	private static float screen_pos_x;
	private static float screen_pos_y;
	private static float scaleFactor;
	private static float screen_pos_x_Bmp;
	private static float screen_pos_y_Bmp;
	private static float scaleFactor_Bmp;
	public static int previousPointCount = 0;
	// private static float x_mean;
	// private static float y_mean;
	private static float sumOfLength;
	private static float x_mean_absolute;
	private static float y_mean_absolute;

	public Display(int width, int height) {
		screen_width = width;
		screen_height = height;
		scaleFactor = 1;
		screen_pos_x = 0;
		screen_pos_y = 0;

		if (((float) screen_width) / screen_height > 4.0 / 3.0) {
			screen_width = screen_height * 4 / 3;
		} else {
			screen_height = screen_width * 3 / 4;
		}

		reSize();
	}

	private static void reset(float x_mean, float y_mean, float sumOfLength) {
		// Display.x_mean = x_mean;
		// Display.y_mean = y_mean;
		Display.x_mean_absolute = x_RelativeToAbsolute(x_mean);
		Display.y_mean_absolute = y_RelativeToAbsolute(y_mean);
		Display.sumOfLength = sumOfLength;
	}

	public static void update(int pointCount, float x_mean, float y_mean,
			float sumOfLength) {
		if (pointCount != previousPointCount) {
			reset(x_mean, y_mean, sumOfLength);
			return;
		}

		// screen_pos_x += (Display.x_mean - x_mean) / scaleFactor;
		// screen_pos_y += (Display.y_mean - y_mean) / scaleFactor;
		scaleFactor *= sumOfLength / Display.sumOfLength;
		screen_pos_x += x_mean_absolute - x_RelativeToAbsolute(x_mean);
		screen_pos_y += y_mean_absolute - y_RelativeToAbsolute(y_mean);

		// Display.x_mean = x_mean;
		// Display.y_mean = y_mean;
		Display.x_mean_absolute = x_RelativeToAbsolute(x_mean);
		Display.y_mean_absolute = y_RelativeToAbsolute(y_mean);
		Display.sumOfLength = sumOfLength;

		if (scaleFactor < 1) {
			scaleFactor = 1;
		} else if (scaleFactor > 10) {
			scaleFactor = 10;
		}

		if (screen_pos_x > screen_width - screen_width / scaleFactor) {
			screen_pos_x = screen_width - screen_width / scaleFactor;
		} else if (screen_pos_x < 0) {
			screen_pos_x = 0;
		}

		if (screen_pos_y > screen_height - screen_height / scaleFactor) {
			screen_pos_y = screen_height - screen_height / scaleFactor;
		} else if (screen_pos_y < 0) {
			screen_pos_y = 0;
		}

	}

	public static void reSize() {
		screen_pos_x_Bmp = screen_pos_x;
		screen_pos_y_Bmp = screen_pos_y;
		scaleFactor_Bmp = scaleFactor;
	}

	public static float x_ScreenPosToBmpPos(float x_screenPos) {
		return (x_RelativeToAbsolute(x_screenPos) - x_RelativeToAbsolute_Bmp(0))
				* scaleFactor_Bmp;
	}

	public static float y_ScreenPosToBmpPos(float y_screenPos) {
		return (y_RelativeToAbsolute(y_screenPos) - y_RelativeToAbsolute_Bmp(0))
				* scaleFactor_Bmp;
	}

	public static float x_BmpPosToScreenPos(float x_BmpPos) {
		return (x_RelativeToAbsolute_Bmp(x_BmpPos) - screen_pos_x)
				* scaleFactor;
	}

	public static float y_BmpPosToScreenPos(float y_BmpPos) {
		return (y_RelativeToAbsolute_Bmp(y_BmpPos) - screen_pos_y)
				* scaleFactor;
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

	/*
	 * private static float x_AbsoluteToRelative_Bmp(float x_absolute) { return
	 * (x_absolute - screen_pos_x_Bmp) * scaleFactor_Bmp; }
	 * 
	 * private static float y_AbsoluteToRelative_Bmp(float y_absolute) { return
	 * (y_absolute - screen_pos_y_Bmp) * scaleFactor_Bmp; }
	 */

	private static float x_RelativeToAbsolute_Bmp(float x_relative_Bmp) {
		return screen_pos_x_Bmp + x_relative_Bmp / scaleFactor_Bmp;
	}

	private static float y_RelativeToAbsolute_Bmp(float y_relative_Bmp) {
		return screen_pos_y_Bmp + y_relative_Bmp / scaleFactor_Bmp;
	}
}
