package org.zerock.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/sample/*")
@Log4j
public class SampleController {

		@RequestMapping("")
		public void basic() {
			log.info("basic............");
		}
		
		@RequestMapping(value = "/basic", method = {RequestMethod.GET,RequestMethod.POST})
		public void basicGet() {
			log.info("basic get............");
		}
		
		@GetMapping("/basicOnlyGet")
		public void basicGet2() {
			log.info("basic get only get............");
		}
		
		/* 
		 * 주석을 한 이유는 DTO단에서 dueDate 를 선언할때 어노테이션으로 DateTimeFormat을 걸어놓았기때문에 굳이 InitBinder를 쓸 이유가 없다.
		 * 
		@InitBinder
		public void initBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			binder.registerCustomEditor(java.util.Date.class,  new CustomDateEditor(dateFormat, false));
		}*/
		
		@PostMapping("/exUploadPost")
		public void MultipartFiledPost(ArrayList<MultipartFile> files) {
			
			files.forEach(file -> {
				log.info("-----------------------------------------");
				log.info("name:" + file.getOriginalFilename());
				log.info("size:" + file.getSize());
			});
		}
		
}
