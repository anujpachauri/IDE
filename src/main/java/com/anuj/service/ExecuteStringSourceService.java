package com.anuj.service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.anuj.compile.StringSourceCompile;
import com.anuj.execute.JavaClassExecutor;

@Service
public class ExecuteStringSourceService {

	private Logger LOGGER=LoggerFactory.getLogger(ExecuteStringSourceService.class);
	
	private static final int RUN_TIME_LIMITED=15;
	private static final int N_THREAD=5;
	private static final String WAIT_WARNING="WAIT";
	private static final String NO_OUTPUT="Nothing";
	
	private static final ExecutorService pool=new ThreadPoolExecutor(N_THREAD, N_THREAD, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(N_THREAD));
	
	
	
	public String execute(String source,String systemIn) {
		
		DiagnosticCollector<JavaFileObject> compileCollector=new DiagnosticCollector<>();
		
		byte[ ] classBytes=StringSourceCompile.compile(source, compileCollector) ;// to do
				
			
		if(classBytes==null) {
			
			List<Diagnostic<? extends JavaFileObject>> compileError=compileCollector.getDiagnostics();
			
			StringBuilder compileErrorRes=new StringBuilder();
			
			for(Diagnostic diagnotic:compileError) {
				compileErrorRes.append("Compilation errot at ");
				compileErrorRes.append(diagnotic.getLineNumber());
				compileErrorRes.append(".");
				compileErrorRes.append(System.lineSeparator());
			}
			
			return compileErrorRes.toString();
		}
			
		Callable<String> runTask=new Callable<String>() {
			
			public String call() throws Exception {
				return JavaClassExecutor.execute(classBytes, systemIn); 
			}
		};
				
		
		Future<String> res=null;
		try {
			res=pool.submit(runTask);			
		} catch (RejectedExecutionException e) {
			return WAIT_WARNING;
		}
		
		String runResult;
		
		try {
			runResult=res.get(RUN_TIME_LIMITED,TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			runResult="Program interrupted";
		}catch (ExecutionException e) {
			runResult=e.getCause().getMessage();
		}catch (TimeoutException e) {
			runResult="Time Limit Exceeded.";
		}finally {
			res.cancel(true);
		}
		return runResult!=null?runResult:NO_OUTPUT;
	}
	
	
}
