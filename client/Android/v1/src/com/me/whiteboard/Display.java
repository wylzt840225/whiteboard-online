package com.me.whiteboard;

public class Display {
	
	public static int screen_width;
	public static int screen_height;
	public static float screen_pos_x;
	public static float screen_pos_y;
	
	public Display(int width, int height) {
		screen_width = width;
		screen_height = height;
		
		if (((float) screen_width) / screen_height > 4.0 / 3.0) {
			screen_width = screen_height * 4 / 3;
		} else {
			screen_height = screen_width * 3 / 4;
		}
	}
	
	public static float x_AbsoluteToRelative (float x_absolute) {
		return x_absolute;
	}
	
	public static float y_AbsoluteToRelative (float y_absolute) {
		return y_absolute;
	}
	
	public static float x_RelativeToAbsolute (float x_relative) {
		return x_relative;
	}
	
	public static float y_RelativeToAbsolute (float y_relative) {
		return y_relative;
	}
}
