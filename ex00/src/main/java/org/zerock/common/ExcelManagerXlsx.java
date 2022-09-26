package org.zerock.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelManagerXlsx {

	public List<HashMap<String, String>> getListXlsxRead(String excel) throws Exception {
	 
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		File file = new File( excel );
		System.out.println("여까지 들어왔다잉~!~!~!");
		if( !file.exists() || !file.isFile() || !file.canRead() ) {
			System.out.println("오류 뱉어 낸다~!~!~!");
			throw new IOException( excel );
		}
		XSSFWorkbook wb = new XSSFWorkbook( new FileInputStream(file) );
		 
		//xls시 이용
		//HSSFWorkbook  wb = new HSSFWorkbook ( new FileInputStream(file) );
		 
		int checkkdy = 1;
		try {
			System.out.println("여까지 들어왔다잉~!~!~!2222222");
		for( int i=0; i < 1; i++ ) {  
			System.out.println("여까지 들어왔다잉~!~!~!33333333");
			System.out.println("wb.getSheetAt(i)??????? : " + wb.getSheetAt(i));
			
		   for( Row row : wb.getSheetAt(i) ) {
			   System.out.println("여까지 들어왔다잉~!~!~!4444444");
			   System.out.println("wb.getSheetAt(0)?? : " + wb.getSheetAt(0));
			   System.out.println("check의 값은? : " + checkkdy);
			   if(checkkdy != 0) {
				   
				   
				   	System.out.println("여까지 들어왔다잉~!~!~!555555555");
		       HashMap<String, String> hMap = new HashMap<String, String>();
		       String valueStr = ""; 
		       int cellLength = (int) row.getLastCellNum();
		       System.out.println("cellLength?? : " + cellLength);
		       for(int j=0; j<cellLength; j++){
		    	   Cell cell = row.getCell(j);
		       
			       if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			    	  valueStr = "";
			       }else{
			    	   switch(cell.getCellType()){
		    	  
			    	   case Cell.CELL_TYPE_STRING :
			            valueStr = cell.getStringCellValue();
			            break;
		    	  
			    	   case Cell.CELL_TYPE_NUMERIC : // 날짜 형식이든 숫자 형식이든 다 CELL_TYPE_NUMERIC으로 인식함.
				            if(DateUtil.isCellDateFormatted(cell)){ // 날짜 유형의 데이터일 경우,
				                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
				                String formattedStr = dateFormat.format(cell.getDateCellValue());
				                valueStr = formattedStr;
				                break;
				            }else{ // 순수하게 숫자 데이터일 경우,
				            	Double numericCellValue = cell.getNumericCellValue();
				            	if(Math.floor(numericCellValue) == numericCellValue){ // 소수점 이하를 버린 값이 원래의 값과 같다면,,
				            		valueStr = numericCellValue.intValue() + ""; // int형으로 소수점 이하 버리고 String으로 데이터 담는다.
				            	}else{
				            		valueStr = numericCellValue + "";
				            	}
				            	break;
				            }
			    	   case Cell.CELL_TYPE_BOOLEAN :
				            valueStr = cell.getBooleanCellValue() + "";
				            break;
			    	   }
		         
			       }
		       
			       hMap.put("cell_"+j ,valueStr);
		           
		       }
		       
		      list.add(hMap);
		 }
			   
		 System.out.println("여까지들어오냥??1234567??? : ");
		 checkkdy = checkkdy + 1;
		 System.out.println("허허허 check의 값이 왜 변경되지 않는거죠?? : " + checkkdy);
		}
		   

		}
		 
		} catch( Exception ex ) {
		ex.printStackTrace();
		}
		 
		return list;
	}


}
