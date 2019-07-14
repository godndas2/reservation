package com.halfdev.pj.comment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController {

	@Autowired CommentDAO commentDao;
	@Autowired CommentVO commentVO;
	
	@Transactional
	@RequestMapping(value="/commentinsert",method=RequestMethod.POST)
	public @ResponseBody List<CommentVO> commentinsert(HttpServletRequest req) {
		commentVO.setParent_seq(req.getParameter("seq"));
		commentVO.setWriter(req.getParameter("writer"));
		commentVO.setContent(req.getParameter("content"));
		commentDao.commentinsert(commentVO);
		List<CommentVO> list = commentDao.commentlist(commentVO);

		return list;
	}
	
	@RequestMapping(value="/commentlist",method=RequestMethod.POST)
	public @ResponseBody List<CommentVO> commentlist(HttpServletRequest req, Model model) {
		commentVO.setParent_seq(req.getParameter("seq"));
		List<CommentVO> list=commentDao.commentlist(commentVO);
		model.addAttribute("list" , list);
		return list;
	}
	
	@Transactional
	@RequestMapping(value="/commentupdate",method=RequestMethod.POST)
	public @ResponseBody CommentVO commentupdate(HttpServletRequest req) {
		commentVO.setComment_seq(req.getParameter("seq"));
		commentVO=commentDao.commentselect(commentVO);
		if(commentVO.getWriter().equals(req.getParameter("id"))){
			commentVO.setContent(req.getParameter("content"));	
			commentDao.commentupdate(commentVO);
			return commentDao.commentselect(commentVO);
		}
		return null;
	}
	
	@RequestMapping(value="/commentdelete",method=RequestMethod.POST)
	public @ResponseBody List<CommentVO> commentdelete(HttpServletRequest req){
		commentVO.setComment_seq(req.getParameter("seq"));
		commentVO=commentDao.commentselect(commentVO);
		if(commentVO.getWriter().equals(req.getParameter("id"))){
			commentDao.commentdelete(commentVO);
			return commentDao.commentlist(commentVO);
		}else 
			return null;
	}
	
}
