package org.zerock.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.common.ExcelRequestManager;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/board/*")
@AllArgsConstructor
public class BoardController {
	
	private BoardService service;
	
	@RequestMapping(value="/list" , method = {RequestMethod.GET, RequestMethod.POST})
	public void list(Criteria cri, Model model) {
		
		log.info("list" + cri);
		model.addAttribute("list" , service.getList(cri));
		
		int total = service.getTotal(cri);

		log.info("total: " + total);

		model.addAttribute("pageMaker", new PageDTO(cri, total));
		
	}
	
	@PostMapping("/register")
	public String register(BoardVO board, RedirectAttributes rttr) {
		
		log.info("==========================");

		log.info("register: " + board);

		if (board.getAttachList() != null) {

			board.getAttachList().forEach(attach -> log.info(attach));

		}

		log.info("==========================");

		service.register(board);

		rttr.addFlashAttribute("result", board.getBno());

		return "redirect:/board/list";
	}
	
	@GetMapping("/register")
	public void register() {
		
	}
	
	@GetMapping({"/get", "/modify"})
	public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri")Criteria cri, Model model) {
		
		log.info("/get or modify");
		model.addAttribute("board", service.get(bno));
	}
	
	@PostMapping("/modify")
	public String modify(BoardVO board, @ModelAttribute("cri")Criteria cri, RedirectAttributes rttr) {
		log.info("modify:" + board);
		
		if (service.modify(board)) {
			rttr.addFlashAttribute("result", "success");
		}
		
		rttr.addAttribute("pageNum", cri.getPageNum());
		rttr.addAttribute("amount",cri.getAmount());
		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		
		return "redirect:/board/list";
	}
	
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") Long bno, @ModelAttribute("cri")Criteria cri, RedirectAttributes rttr) {
		
		log.info("remove..." + bno);
		
		List<BoardAttachVO> attachList = service.getAttachList(bno);
		
		if(service.remove(bno)) {
			
			deleteFiles(attachList);
			
			rttr.addFlashAttribute("result", "success");
		}
		
		rttr.addAttribute("pageNum", cri.getPageNum());
		rttr.addAttribute("amount",cri.getAmount());
		rttr.addAttribute("type", cri.getType());
		rttr.addAttribute("keyword", cri.getKeyword());
		return "redirect:/board/list" + cri.getListLink();
	}
	
	@GetMapping(value = "/getAttachList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno) {

	log.info("getAttachList " + bno);

	return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);

	}
	
	private void deleteFiles(List<BoardAttachVO> attachList) {
	    
	    if(attachList == null || attachList.size() == 0) {
	      return;
	    }
	    
	    log.info("delete attach files...................");
	    log.info(attachList);
	    
	    attachList.forEach(attach -> {
	      try {        
	        Path file  = Paths.get("C:\\upload\\"+attach.getUploadPath()+"\\" + attach.getUuid()+"_"+ attach.getFileName());
	        
	        Files.deleteIfExists(file);
	        
	        if(Files.probeContentType(file).startsWith("image")) {
	        
	          Path thumbNail = Paths.get("C:\\upload\\"+attach.getUploadPath()+"\\s_" + attach.getUuid()+"_"+ attach.getFileName());
	          
	          Files.delete(thumbNail);
	        }
	
	      }catch(Exception e) {
	        log.error("delete file error" + e.getMessage());
	      }//end catch
	    });//end foreachd
	  }
	
	
	
	  @RequestMapping(value ="/adms/reserve/applicant/list.do")
	  public String listApplicant(HttpServletRequest request, ModelMap model) throws Exception {
	 
	    return "redirect:/board/list";
	}
	  
	  
	  @RequestMapping(value ="/adms/reserve/applicant/create_action.do", method = {RequestMethod.POST})
	  public String createApplicant_action(@ModelAttribute("searchVO") BoardVO searchVO,
	          RedirectAttributes redirectAttributes,
	          HttpServletRequest request,
	          final MultipartHttpServletRequest multiRequest,
	          ModelMap model) throws Exception {
	   
	  //LoginVO loginVO = loginService.getLoginInfo();
	   
	  String errstr = null; 
	  Map<String, Object> resMap = new HashMap<String, Object>();
	  
	  System.out.println("asdfasfasdfasfasfsaf여까진들어오냐???????????????????????????");
	   
	  try{
	      
	      ExcelRequestManager em = new ExcelRequestManager();
	      final Map<String, MultipartFile> files = multiRequest.getFileMap();
	      List<HashMap<String,String>> apply =null;
	      
	      apply = em.parseExcelSpringMultiPart(files,"applicant", 0, "", "tmp");
	      
	      System.out.println("asdfasfasdfasfasfsaf여까진들어오냐???????????????????????????22");
	      System.out.println("files : " + files);  
	      System.out.println("apply.size()?? : " + apply.size());  
	      for(int i = 1; i < apply.size(); i++){
	    	  
	    	  System.out.println("asdfasfasdfasfasfsaf여까진들어오냐???????????????????????????777");
	    	  System.out.println("apply.size()?? : " + apply.size()); 
	    	  System.out.println("i의값 : " + i); 
	         //searchVO.setResv_program_type(apply.get(i).get("cell_0"));
	         searchVO.setTitle(apply.get(i).get("cell_0"));
	         System.out.println("aa : " + apply.get(i).get("cell_0"));
	         //searchVO.setResv_program(apply.get(i).get("cell_1"));
	         searchVO.setContent(apply.get(i).get("cell_1"));
	         System.out.println("bb : " + apply.get(i).get("cell_1"));
	         
	         searchVO.setWriter(apply.get(i).get("cell_2"));
	         System.out.println("cc : " + apply.get(i).get("cell_2"));
	         //searchVO.setResv_biz_name(apply.get(i).get("cell_2"));
	         
	         //String text = String.join(",", apply.get(i).get("cell_3"));
	         //System.out.println("dd : " + text);
	       
	         //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");         
	         //Date date = formatter.parse(text);
	        
	         //searchVO.setRegdate(date);
                 
	         errstr = service.register(searchVO, errstr);
	        
	          
	      }
	      System.out.println("errstr ?? : " + errstr);
	      if(errstr != "") {
	    	  resMap.put("errstr", errstr);
	      }
	      if(errstr == null) {
		      resMap.put("msg", "txt.success");
	      }
	   
	  }catch(Exception e){
	      System.out.println(e.toString());
	      resMap.put("res", "error");
	      resMap.put("msg", "txt.fail");
	  }
	   
	   
		  redirectAttributes.addFlashAttribute("resMap", resMap);
		  return "redirect:/board/list";
	  }
	  
	  
	  @RequestMapping(value ="/excel/excelDownload", method = {RequestMethod.POST})
	    public void excelDownload(HttpServletResponse response) throws IOException {
//	        Workbook wb = new HSSFWorkbook();
	        Workbook wb = new XSSFWorkbook();
	        Sheet sheet = wb.createSheet("첫번째 시트");
	        Row row = null;
	        Cell cell = null;
	        int rowNum = 0;

	        // Header
	        row = sheet.createRow(rowNum++);
	        cell = row.createCell(0);
	        cell.setCellValue("타이틀적는곳");
	        cell = row.createCell(1);
	        cell.setCellValue("내용적는곳");
	        cell = row.createCell(2);
	        cell.setCellValue("저자적는곳");

	        // Body
	        /*
	        for (int i=0; i<3; i++) {
	            row = sheet.createRow(rowNum++);
	            cell = row.createCell(0);
	            cell.setCellValue(i);
	            cell = row.createCell(1);
	            cell.setCellValue(i+"_name");
	            cell = row.createCell(2);
	            cell.setCellValue(i+"_title");
	        }
	        */

	        // 컨텐츠 타입과 파일명 지정
	        response.setContentType("ms-vnd/excel");
//	        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
	        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

	        // Excel File Output
	        wb.write(response.getOutputStream());
	    }
 

	
}
