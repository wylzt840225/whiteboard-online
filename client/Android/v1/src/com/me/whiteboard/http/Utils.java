package com.me.whiteboard.http;

import java.util.List;

public class Utils {
	public static String join(List<String> sa,String splitter)
	{
		StringBuilder strb=new StringBuilder();
		if(sa.size()==0)return "";
		strb.append(sa.get(0));
		for(int i=1;i<sa.size();i++)
		{
			strb.append(splitter);
			strb.append(sa.get(i));
		}
		return strb.toString();
	}
	
}
