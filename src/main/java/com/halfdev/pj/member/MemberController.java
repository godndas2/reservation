package com.halfdev.pj.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.halfdev.pj.loginapi.KakaoLoginAPI;
import com.halfdev.pj.loginapi.NaverLoginBO;

@Controller
public class MemberController {

	@Autowired private MemberDAO memberDao;
	@Autowired private MemberVO memberVO;
	@Autowired BCryptPasswordEncoder passwordEncoder;
	
	/* NaverLoginBO */
	private NaverLoginBO naverLoginBO;
	private String apiResult = null;
	
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO = naverLoginBO;
	}
	
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
	public String login(Model model, HttpSession session) {
//		String naverAuthUrl = naverLoginBO.getAuthorizationUrl(session);
//		System.out.println("네이버:" + naverAuthUrl);
//		System.out.println("네이버:" + naverAuthUrl);
//		model.addAttribute("url", naverAuthUrl);
		
		if(session.getAttribute("userid") != null)
		{
			return "logout";
		}else {
			return "login";
		}
	}
	
	//네이버 로그인 성공시 callback호출 메소드
//		@RequestMapping(value = "/callback", method = { RequestMethod.GET, RequestMethod.POST })
//		public String callback(Model model, @RequestParam String code, @RequestParam String state, HttpSession session)
//				throws IOException, ParseException {
//			System.out.println("여기는 callback");
//			OAuth2AccessToken oauthToken;
//			oauthToken = naverLoginBO.getAccessToken(session, code, state);
//			//1. 로그인 사용자 정보를 읽어온다.
//			apiResult = naverLoginBO.getUserProfile(oauthToken); //String형식의 json데이터
//			/** apiResult json 구조
//			{"resultcode":"00",
//			"message":"success",
//			"response":{"id":"33666449","nickname":"shinn****","age":"20-29","gender":"M","email":"shinn0608@naver.com","name":"\uc2e0\ubc94\ud638"}}
//			**/
//			//2. String형식인 apiResult를 json형태로 바꿈
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse(apiResult);
//			JSONObject jsonObj = (JSONObject) obj;
//			//3. 데이터 파싱
//			//Top레벨 단계 _response 파싱
//			JSONObject response_obj = (JSONObject)jsonObj.get("response");
//			//response의 nickname값 파싱
//			String nickname = (String)response_obj.get("nickname");
//			System.out.println(nickname);
//			//4.파싱 닉네임 세션으로 저장
//			session.setAttribute("sessionid",nickname); //세션 생성
//			model.addAttribute("result", apiResult);
//			return "login";
//		}

	@RequestMapping(value="/memberlogin",method=RequestMethod.POST)
	public @ResponseBody String  memberlogin(HttpServletRequest req){
		String id=req.getParameter("id");
		String pw=req.getParameter("password");	
		memberVO.setId(id);
		memberVO=memberDao.memberlogin(memberVO);
		if(!ObjectUtils.isEmpty(memberVO)&&!id.isEmpty()) {
			if(passwordEncoder.matches(pw, memberVO.getPassword())) {
				HttpSession session = req.getSession();
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
