package com.halfdev.pj.room;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoomController {

	@Autowired RoomDAO roomDao;
	@Autowired RoomVO roomVO;
	@Autowired private ServletContext context;

	@RequestMapping(value = "/room", method = RequestMethod.GET)
	public String room() {
		return "room";
	}

	@RequestMapping(value = "/roomselect", method = RequestMethod.POST)
	public @ResponseBody List<String> roomselect(HttpServletRequest req) {
		String room = req.getParameter("room");
		List<String> imglist = new ArrayList<String>();

		String path = context.getRealPath("/WEB-INF/img/"+room);
		File dirFile = new File(path);
		
		File[] fileList=dirFile.listFiles();
		for(File tempFile : fileList) {
			  if(tempFile.isFile()) {
				  imglist.add(tempFile.getName());
			  }
			}
		return imglist;
	}
	
	@RequestMapping(value = "/roomcheck", method = RequestMethod.POST)
	public @ResponseBody String roomcheck(HttpServletRequest req){
		String checkin=req.getParameter("checkin");
		int staydays = Integer.parseInt(req.getParameter("stay"));
		String roomType = req.getParameter("roomType");
		int needrooms = Integer.parseInt(req.getParameter("need_rooms"));
		
		int max = 0;
		if(roomType.equals("room1")) {
			 max = 31;
		}else if(roomType.equals("room2")) {
			 max = 62;
		}else {
			max = 93;
		}
		roomVO.setRoomType(roomType);
	    roomVO.setRoomDate(checkin);
		//checkin 날짜에 해당하는 날의 객실 인덱스를 가져옴
		int seq = roomDao.roomcheck(roomVO).getRoomSeq();
		System.out.println("seq : " + seq);
		//머무는 기간동안 남은 객실 수 체크
		for(int i = 0; i < staydays; i++) {
			if(seq + i <= max) {
				roomVO.setRoomSeq(seq + i);
			}else {
				roomVO.setRoomSeq(seq + i - 31);
			}	
			if(roomDao.stayroomcheck(roomVO).getRoomStay() - needrooms < 0) {
			
				return roomVO.getRoomDate();		
			}
		}
		return "ok";
	}
	
	//매일 정각에 실행 (객실 테이블 인덱스 관리)
	@Scheduled(cron="0 0 0 * * ?")
	@Transactional
	public void roomchange(){
		  TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
	       Calendar calendar = Calendar.getInstance(timeZone);
	       calendar.setTimeZone(timeZone);
	       String pattern = "yyyy-MM-dd";
	       SimpleDateFormat format = new SimpleDateFormat(pattern);
	       format.setTimeZone(timeZone);
	       
	       List<Integer> seq_list=new ArrayList<Integer>();
	       
	       for(int i = 1; i < 4; i++) {  
	    	   roomVO.setRoomDate(format.format(calendar.getTime()));
		       roomVO.setRoomType("room" + i);
		        
		       seq_list.add(roomDao.roomcheck(roomVO).getRoomSeq());
	       } 
	       
	       calendar.add(Calendar.DATE,31);
	       
	       for(int i = 0; i <3; i++) {
	    	   roomVO.setRoomDate(format.format(calendar.getTime()));
	    	   roomVO.setRoomSeq(seq_list.get(i));
	    	   roomDao.roomchange(roomVO);
	       }
	}
	
	@RequestMapping(value = "/remainingrooms", method = RequestMethod.POST)
	public @ResponseBody List<RoomVO> remainingrooms(HttpServletRequest req){
		List<RoomVO> list = new ArrayList<RoomVO>();
		list = roomDao.remainingrooms(req.getParameter("room"));
		return list;
	}
	
	//룸 테이블 세팅 클래스 junit으로 세팅
	@Transactional
	@RequestMapping(value = "/room1", method = RequestMethod.POST)
	public void room1() {

		  TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
	       Calendar calendar = Calendar.getInstance(tz);
	       calendar.set(2019, Calendar.JULY , 16);
	       String pattern = "yyyy-MM-dd";
	       SimpleDateFormat format = new SimpleDateFormat(pattern);
	       format.setTimeZone(tz);
	       System.out.println(format.format(calendar.getTime()));
	       
	   
		for(int i=1; i<4;i++) {
			roomVO.setRoomType("room"+i);
		    for(int j=0;j<31;j++) {
		    	   calendar.add(Calendar.DATE, j);
		    	   roomVO.setRoomDate(format.format(calendar.getTime()));
		    	   System.out.println(format.format(calendar.getTime()));
		    	   roomDao.roomset(roomVO);
		    	   calendar.set(2019, Calendar.JULY , 16);
		       }
		}
		
	}
}
