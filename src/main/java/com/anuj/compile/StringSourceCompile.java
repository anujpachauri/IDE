package com.anuj.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;



public class StringSourceCompile {

	
	private static Map<String,JavaFileObject> fileObjectMap=new ConcurrentHashMap<>();
	
	
	private static Pattern CLASS_PATTERN=Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s");
	
	
	public static byte[] compile(String source,DiagnosticCollector<JavaFileObject> compileCollector)
	{
		
		JavaCompiler compile=ToolProvider.getSystemJavaCompiler();
		
		JavaFileManager javaFileManager=new TmpJavaFileManager(compile.getStandardFileManager(compileCollector, null, null));
		
		
		Matcher matcher=CLASS_PATTERN.matcher(source);
		
		String className;
		if(matcher.find()) {
			className=matcher.group(1);
		}else {
			throw new IllegalArgumentException("No valid class");
		}
		
		JavaFileObject sourceJavaFileObject=new TmpJavaFileObject(className,source);
		
		Boolean result=compile.getTask(null, javaFileManager, compileCollector, null, null, Arrays.asList(sourceJavaFileObject)).call();
		
		
		JavaFileObject byteJavaFileObject=fileObjectMap.get(className);
		if(result && byteJavaFileObject!=null) {
			return ((TmpJavaFileObject)byteJavaFileObject).getCompileBytes();
		}
		
		return null;
		
	}
	
	 static class TmpJavaFileManager extends ForwardingJavaFileManager<JavaFileManager>{

			protected TmpJavaFileManager(JavaFileManager fileManager) {
				super(fileManager);		
			}
			
			public JavaFileObject getJavaFileForInput(JavaFileManager.Location location,String className,JavaFileObject.Kind kind) throws IOException {
			
				JavaFileObject javaFileObject=fileObjectMap.get(className);
				if(javaFileObject==null) {
					return super.getJavaFileForInput(location, className, kind);
				}
				
				return javaFileObject;
				
			}
			
			
			@Override
	        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
	            JavaFileObject javaFileObject = new TmpJavaFileObject(className, kind);
	            fileObjectMap.put(className, javaFileObject);
	            return javaFileObject;
	        }
			
			
			
			
		}
	 
	 
	 static class TmpJavaFileObject extends SimpleJavaFileObject{

		 private String source;
		 private ByteArrayOutputStream outputStream;
		protected TmpJavaFileObject(String name , String source) {
			super(URI.create("String:///" +name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source=source;
		}
		
		
		public TmpJavaFileObject(String name,Kind kind) {
			super(URI.create("String:///" +name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source=null;
		}
		
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			
			if(source==null) {
				throw new IllegalArgumentException("source==null");
			}
			return source;
		}
		
		
		public OutputStream openOutputStream()
		{
			outputStream=new ByteArrayOutputStream();
			return outputStream;
		}
		
		public byte[] getCompileBytes() {
			return outputStream.toByteArray();
		}
		
		
		}
		 
	 }

	
	
	


