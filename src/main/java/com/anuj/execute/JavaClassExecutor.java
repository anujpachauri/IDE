package com.anuj.execute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaClassExecutor {

	
	public static String execute(byte[] classbyte,String systemIn) {
		
		
		ClassModifier cm=new ClassModifier(classbyte);
		
		byte[] modifyBytes=cm.modifyUTF8Constant("java/lang/System", "com/anuj/execute/HackSystem");
		modifyBytes=cm.modifyUTF8Constant("java/util/Scanner", "com/anuj/execute/HackScanner");
		
		((HackInputStream)HackSystem.in).set(systemIn);
		
		
		
		HotSwapClassLoader classLoader=new HotSwapClassLoader();
		
		Class clazz=classLoader.loadByte(modifyBytes);
		
		
		try {
			
			Method mainMethod=clazz.getMethod("main", new Class[] {String[].class});
			mainMethod.invoke(null, new String[] {null});
			
		} catch (NoSuchMethodException e) {
		   e.printStackTrace();
		}catch(IllegalAccessException e) {
			e.printStackTrace();
		}catch(InvocationTargetException e) {
			e.getCause().printStackTrace(HackSystem.err);
		} 
		String res=HackSystem.getBufferString();
		HackSystem.closeBuffer();
		return res;
	}
}
