package com.halfdev.pj.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {

	@Autowired private MemberDAO memberDao;
	@Autowired private MemberVO memberVO;
	@Autowired BCryptPasswordEncoder passwordEncoder;
	
	
	@RequestMapping(value="sessioncheck",method=RequestMethod.POST)
	public @ResponseBody String sessioncheck(HttpServletRequest req) {
		HttpSession session=req.getSession(true);
		if(session.getAttribute("userid") == null) {
			return "logout";
		}else {
			return session.getAttribute("userid").toString();
		}
	}
	
	@RequestMapping(value="/emailcheck",method = RequestMethod.POST)
	public @ResponseBody boolean emailcheck(HttpServletRequest req){
		String email=req.getParameter("email");
		if(ObjectUtils.isEmpty(memberDao.emailcheck(email))) {
			return true;
		}else {
			return false;
		}
	}
	
	@RequestMapping(value="/idcheck",method=RequestMethod.POST)
	public @ResponseBody boolean idcheck(HttpServletRequest req){
		String id=req.getParameter("id");
		if(memberDao.idcheck(id)!=null) {
			return false;
		}else {
			return true;
		}
	}
	
	@RequestMapping(value="/findId",method=RequestMethod.POST)
	public @ResponseBody String findId(HttpServletRequest req){
		String email=req.getParameter("email");
		memberVO = memberDao.emailcheck(email);

		if(!ObjectUtils.isEmpty(memberVO)) {
			return memberVO.getId(); 
		}else {
			return null;		
		}
	}
	
	// 회원가입
	@RequestMapping(value="/join",method=RequestMethod.GET)
	public String join() {return "join";}
	
//	@Transactional
	@RequestMapping(value="/memberjoin",method=RequestMethod.POST)
	public @ResponseBody boolean memberjoin(HttpServletRequest req){
		String id = req.getParameter("id");
		String email = req.getParameter("email");

		if(!id.isEmpty() && !email.isEmpty()) {
			if(!ObjectUtils.isEmpty(memberDao.idcheck(id)) && !ObjectUtils.isEmpty(memberDao.emailcheck(email))) {
				return false;
			}else {
				String pw = req.getParameter("password");	
				String encodepw = passwordEncoder.encode(pw);
	
				memberVO.setId(id);
				memberVO.setEmail(email);
				memberVO.setPassword(encodepw);
				memberDao.memberjoin(memberVO);
				HttpSession session=req.getSession(false);
				session.setAttribute("userid", id);
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public String login(HttpServletRequest req) {
		HttpSession session = req.getSession();
		
		if(session.getAttribute("userid") != null)
		{
			return "logout";
		}else {
			return "login";
		}
	}
	
	@RequestMapping(value="/memberlogin",method=RequestMethod.POST)
	public @ResponseBody String  memberlogin(HttpServletRequest req){
		String id=req.getParameter("id");
		String pw=req.getParameter("password");	
		
		memberVO.setId(id);
		memberVO=memberDao.memberlogin(memberVO);
		if(!ObjectUtils.isEmpty(memberVO)&&!id.isEmpty()) {
			if(passwordEncoder.matches(pw, memberVO.getPassword())) {
				HttpSession session=req.getSession();
				session.setAttribute("userid",id);		
				return id;
			}else {
				return null;
			}
		}
		return null;
	}
	
	@RequestMapping(value="/memberlogout",method = RequestMethod.GET)
	public String memberlogout(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		return "logout";
	}
}
