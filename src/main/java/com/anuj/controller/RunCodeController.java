package com.anuj.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anuj.service.ExecuteStringSourceService;




@Controller
public class RunCodeController {

	private Logger LOGGER=LoggerFactory.getLogger(RunCodeController.class);
	
	@Autowired
	private ExecuteStringSourceService executeSourceService;
	
	
	private static final String defaultSource="public class Run {\n"
			+ "  public static void main(String [] args) {\n"
			+ "               \n"
			+ "        }\n"
			+ "}";
			
	
	@GetMapping(path = "/")
	public String entry(Model model) {
		model.addAttribute("lastSource",defaultSource);
		return "ide";
	}
	
	@PostMapping(path = "/run")
	public String runCode(@RequestParam("source") String source,@RequestParam("systemIn") String systemIn,Model model) {
		String runResult=executeSourceService.execute(source, systemIn);// to do
		runResult=runResult.replaceAll(System.lineSeparator(), "<br/>");
		model.addAttribute("lastSource", source);
		model.addAttribute("lastSystemIn", systemIn);
		model.addAttribute("runResult", runResult);
		System.out.println(runResult);
		return "ide";
	}
	
	
	
	
}
