package org.zerock.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
	
	private int startPage;
	private int endPage;
	private boolean prev, next;
	
	private int total;
	private Criteria cri;
	
	public PageDTO(Criteria cri, int total) {
		
		this.cri = cri;
		this.total = total;
		
		/*
		 * 페이징 처리 할때 요즘은 이렇게 자바단에서 작업하지않고
		 * 쿼리단에서 작업하는데, 그예시로
		 * 
		 * 쿼리문뒤에 
		 * PAGE_QUEARY WHERE ROWNUM <<이구문을 사용해서 페이징 작업을 사용하는데
		 * 해당사항은 구글링해서 어떻게 쓰는지 확인해 볼것!
		 * 
		 * */
		this.endPage = (int) (Math.ceil(cri.getPageNum() / 10.0)) * 10;
		this.startPage = this.endPage - 9;
		
		int realEnd = (int) (Math.ceil((total * 1.0) / cri.getAmount() ) );
		
		if(realEnd < this.endPage) {
			this.endPage = realEnd;
		}
		
		this.prev = this.startPage > 1;
		
		this.next = this.endPage < realEnd;
	}
}
