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
	
	@RequestMapping(value="/checkIdMail",method=RequestMethod.POST)
	public @ResponseBody String checkIdMail(HttpServletRequest req){
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
	
	@Transactional
	@RequestMapping(value="/memberjoin",method=RequestMethod.POST)
	public @ResponseBody boolean memberjoin(HttpServletRequest req){
		String id = req.getParameter("id");
		String email = req.getParameter("email");

		if(!id.isEmpty()&&!email.isEmpty()) {
			if(!ObjectUtils.isEmpty(memberDao.idcheck(id))&&!ObjectUtils.isEmpty(memberDao.emailcheck(email))) {
				return false;
				
			}else {
				String pw = req.getParameter("password");	
				String encodepw = passwordEncoder.encode(pw);
	
				memberVO.setId(id);
				memberVO.setEmail(email);
				memberVO.setPassword(encodepw);
				memberDao.memberjoin(memberVO);
				System.out.println(">>>>>>>>>>>>>>>>" + encodepw);
				System.out.println("13123113123" + pw);
				HttpSession session=req.getSession(false);
				session.setAttribute("userid", id);
				return true;
			}
		}
		return false;
	}
}
