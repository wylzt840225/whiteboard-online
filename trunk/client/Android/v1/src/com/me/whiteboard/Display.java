package com.me.whiteboard;

public class Display {
	
	public static int screen_width;
	public static int screen_height;
	public static float screen_pos_x;
	public static float screen_pos_y;
	public static float scaleFactor;
	public static int previousPointCount = 0;
	public static float x_mean;
	public static float y_mean;
	public static float sumOfLength;
	
	public Display(int width, int height) {
		screen_width = width;
		screen_height = height;
		scaleFactor=1;
		screen_pos_x=0;
		screen_pos_y=0;
		if (((float) screen_width) / screen_height > 4.0 / 3.0) {
			screen_width = screen_height * 4 / 3;
		} else {
			screen_height = screen_width * 3 / 4;
		}
	}
	
	private static void reset(float x_mean, float y_mean, float sumOfLength) {
		Display.x_mean = x_mean;
		Display.y_mean = y_mean;
		Display.sumOfLength = sumOfLength;
	}
	
	public static void update(int pointCount, float x_mean, float y_mean, float sumOfLength) {
		if (pointCount != previousPointCount) {
			reset(x_mean, y_mean, sumOfLength);
			return;
		}
		
		screen_pos_x += (Display.x_mean-x_mean) / scaleFactor;
		screen_pos_y += (Display.y_mean-y_mean) / scaleFactor;
		Display.x_mean=x_mean;
		Display.y_mean=y_mean;
		scaleFactor *= sumOfLength / Display.sumOfLength;
		if(scaleFactor<1)
		{
			scaleFactor=1;
		}
		if(scaleFactor>10)
		{
			scaleFactor=10;
		}
		
		if(screen_pos_x>screen_width-screen_width/scaleFactor)
		{
			screen_pos_x=screen_width-screen_width/scaleFactor;
		}
		if(screen_pos_x<0)screen_pos_x=0;
		
		if(screen_pos_y>screen_height-screen_height/scaleFactor)
		{
			screen_pos_y=screen_height-screen_height/scaleFactor;
		}
		if(screen_pos_y<0)screen_pos_y=0;
		
		
		Display.sumOfLength=sumOfLength;
	}
	
	public static float x_AbsoluteToRelative (float x_absolute) {
		return (x_absolute - screen_pos_x) * scaleFactor;
	}
	
	public static float y_AbsoluteToRelative (float y_absolute) {
		return (y_absolute - screen_pos_y) * scaleFactor;
	}
	
	public static float x_RelativeToAbsolute (float x_relative) {
		return screen_pos_x + x_relative / scaleFactor;
	}
	
	public static float y_RelativeToAbsolute (float y_relative) {
		return screen_pos_y + y_relative / scaleFactor;
	}
}
