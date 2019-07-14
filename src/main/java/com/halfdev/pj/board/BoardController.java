package com.halfdev.pj.board;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mysql.jdbc.StringUtils;

@Controller
public class BoardController {

	@Autowired private BoardVO boardVO;
	@Autowired private BoardDAO boardDao;
	
	@RequestMapping(value = "/board", method = RequestMethod.GET)
	public String boardList(Model model) {
		model.addAttribute("list",boardDao.boardList(0));
		model.addAttribute("count",boardDao.boardCount());
		// 레코드 개수
		return "board";
	}
	
	@RequestMapping(value = "/boardpaging", method = RequestMethod.POST)
	public @ResponseBody List<BoardVO> boardpaging(Model model,HttpServletRequest req) {

		int i=Integer.parseInt(req.getParameter("i"));
		model.addAttribute("count",boardDao.boardCount());
		return boardDao.boardList((i-1)*10);
	}
	@RequestMapping(value = "/boardwriteform", method = RequestMethod.POST)
	public String boardwriteform() {
		return "boardWrite";
	}
	@RequestMapping(value = "/boardreadform", method = RequestMethod.POST)
	public String boardreadform() {
		return "boardRead";
	}
	@Transactional
	@RequestMapping(value = "/boardinsert", method = RequestMethod.POST)
	public @ResponseBody List<BoardVO> submit(MultipartHttpServletRequest req) throws IllegalStateException, IOException
 {	 		
	
		List<MultipartFile> mf = req.getFiles("file[]");
		String id=req.getParameter("writer");
	
		if(mf.get(0).getSize()>0) {	
			boardVO.setFilepath(boardDao.uploadfiles(mf,id));
		}else {
			boardVO.setFilepath(null);
		}

		String title=req.getParameter("title");
		String content=req.getParameter("content");
		String writer=req.getParameter("writer");
		boardVO.setTitle(title);
		boardVO.setContent(content);
		boardVO.setWriter(writer);
		boardVO.setSecurity(req.getParameter("lock"));
		boardDao.boardinsert(boardVO);
		return boardDao.boardList(0);
	}
	@Transactional
	@RequestMapping(value = "/boardread", method = RequestMethod.POST)
	public  @ResponseBody BoardVO boardread(HttpServletRequest req) {
		boardVO.setSeq(Integer.parseInt(req.getParameter("seq")));
		return boardDao.selectread(boardVO);
	}
	
	@RequestMapping(value="/fileread", method = RequestMethod.POST)
		public @ResponseBody List<String> readfile(HttpServletRequest req){

		return boardDao.fileread(req.getParameter("path"));
	}
	@Transactional
	@RequestMapping(value="/fileupdate", method = RequestMethod.POST)
	public @ResponseBody boolean fileupdate(HttpServletRequest req){

		String[] list=req.getParameterValues("list");
		boardVO.setSeq(Integer.parseInt(req.getParameter("seq")));
		boardVO=boardDao.selectread(boardVO);

		boardDao.updatefile(list,boardVO.getFilepath());

		return true;
}
	@Transactional
	@RequestMapping(value = "/securitycheck", method = RequestMethod.POST)
	public  @ResponseBody boolean securitycheck(HttpServletRequest req) {
		String id=req.getParameter("sessionid");
		String seq=req.getParameter("seq");

		if(id.equals("admin")) {
			return true;
		}
		boardVO.setSeq(Integer.parseInt(seq));
		boardVO.setWriter(id);

		if(boardDao.securitycheck(boardVO)==null) {
			return false;
		}else {
			return true;
		}
	}
	@Transactional
	@RequestMapping(value = "/boardupdate", method = RequestMethod.POST)
	public String boardupdate(MultipartHttpServletRequest req) throws IllegalStateException, IOException {	
		List<MultipartFile> mf = req.getFiles("file[]");
		boardVO.setSeq(Integer.parseInt(req.getParameter("seq")));
		if(mf.get(0).getSize()>0) {
			boardVO=boardDao.selectread(boardVO);
			boardDao.addfiles(mf, boardVO.getFilepath());
		}
		String title=req.getParameter("title");
		String content=req.getParameter("content");
		
		
		boardVO.setTitle(title);
		boardVO.setContent(content);	
		boardDao.boardupdate(boardVO);
		return "board";
	}
	
	@Transactional
	@RequestMapping(value = "/boarddelete", method = RequestMethod.POST)
	public String boarddelete(HttpServletRequest req) {
		boardVO.setSeq(Integer.parseInt(req.getParameter("seq")));
		boardVO=boardDao.selectread(boardVO);
		if(!StringUtils.isNullOrEmpty(boardVO.getFilepath())) {
			boardDao.filedelete(boardVO.getFilepath());
		}
		boardDao.boarddelete(boardVO);
		
		return "board";
	}
	
}
