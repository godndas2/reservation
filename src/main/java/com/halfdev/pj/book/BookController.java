package com.halfdev.pj.book;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.halfdev.pj.room.RoomDAO;
import com.halfdev.pj.room.RoomVO;

@Controller
public class BookController {

	@Autowired
	BookDAO bookDAO;
	@Autowired
	BookVO bookVO;
	@Autowired
	RoomVO roomVO;
	@Autowired
	RoomDAO roomDAO;
	

	@Transactional
	@RequestMapping(value="/bookinsert" ,method = RequestMethod.POST)
	public String bookinsert(HttpServletRequest req) {
		bookVO.setBook_checkin(req.getParameter("checkin"));
		bookVO.setBook_checkout(req.getParameter("checkout"));
		bookVO.setBook_pay(req.getParameter("pay"));
		bookVO.setBook_person(req.getParameter("person"));
		bookVO.setBook_type(req.getParameter("room"));
		bookVO.setStay(req.getParameter("stay"));
		int stay=Integer.parseInt(req.getParameter("stay"));
		int needrooms=Integer.parseInt(req.getParameter("needrooms"));
		
		int max=0;
		if(bookVO.getBook_type().equals("room1")) {
			 max=31;
		}else if(bookVO.getBook_type().equals("room2")) {
			 max=62;
		}else {
			max=93;
		}
		
		
		bookDAO.bookinsert(bookVO);	
		
		roomVO.setRoomDate(bookVO.getBook_checkin());
		roomVO.setRoomType(bookVO.getBook_type());
		roomVO=roomDAO.roomcheck(roomVO);
		
		int seq=roomVO.getRoomSeq();
		roomVO.setRoomStay(roomVO.getRoomStay()-needrooms);
		
		for(int i=0;i<stay;i++) {
			if(seq+i<=max) {
				roomVO.setRoomSeq(seq+i);
			}else {
				roomVO.setRoomSeq(seq+i-31);
			}
			roomDAO.roomupdate(roomVO);
		}
		
		return "booking";
	}
	
	@RequestMapping(value="/mybookselect" ,method=RequestMethod.POST)
	public @ResponseBody List<BookVO> mybookselect(HttpServletRequest req) {
		String id = req.getParameter("id");
		return bookDAO.mybookselect(id);
	}
	
	@Transactional
	@RequestMapping(value="/mybookdelete" ,method=RequestMethod.POST)
	public @ResponseBody boolean mybookdelete(HttpServletRequest req) {
		int rooms;
		int max;
		String type=req.getParameter("type");
		String checkin=req.getParameter("checkin");
		int pay=Integer.parseInt(req.getParameter("pay"));
		int stay=Integer.parseInt(req.getParameter("stay"));
		pay=pay/stay;
		
		if(type.equals("room1")) {
			rooms=pay/10;
		}else if(type.equals("room2")) {
			rooms=pay/19;
		}else {
			rooms=pay/36;
		}
		roomVO.setRoomType(type);
		roomVO.setRoomDate(checkin);
		
		roomVO=roomDAO.roomcheck(roomVO);
		
		int roomseq=roomVO.getRoomSeq();
		
		if(type.equals("room1")) {
			 max=31;
		}else if(type.equals("room2")) {
			 max=62;
		}else {
			max=93;
		}
		
		roomVO.setRoomStay(roomVO.getRoomStay()+rooms);
		for(int i=0;i<stay;i++) {			
			if(roomseq+i<=max) {
				roomVO.setRoomSeq(roomseq+i);
			}else {
				roomVO.setRoomSeq(roomseq+i-31);
			}
			roomVO.setRoomSeq(roomseq+i);
			roomDAO.roomupdate(roomVO);
			
		}
		
		bookDAO.mybookdelete(req.getParameter("seq"));
		
		return true;
	}
}
