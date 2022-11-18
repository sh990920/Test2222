package com.korea.bbs;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.BoardDAO;
import util.Common;
import util.Paging;
import vo.BoardVO;

@Controller
public class BoardController {

	@Autowired
	HttpServletRequest request;

	HttpSession session;

	BoardDAO board_dao;
	public void setBoard_dao(BoardDAO board_dao) {
		this.board_dao = board_dao;
	}

	//게시글 전체목록 조회
	@RequestMapping( value = {"/", "/list.do"} )
	public String list( Integer page ) {

		//list.do? ---------> null
		//list.do?page= ----> empty

		int nowPage = 1;

		if( page != null ) {
			nowPage = page;
		}

		//한 페이지에 표시되는 게시물의 시작번호와 끝번호를 계산
		int start = (nowPage - 1) * Common.BLOCKLIST + 1;
		int end = start + Common.BLOCKLIST - 1;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("start", start);
		map.put("end", end);

		//페이지별 목록조회
		List<BoardVO> list = board_dao.selectList(map);

		//전체 게시물의 갯수 구하기
		int row_total = board_dao.getRowTotal();

		//위의 정보들을 기반으로 하단의 페이지메뉴를 생성
		String pageMenu = Paging.getPaging("list.do", nowPage, row_total, Common.BLOCKLIST, Common.BLOCKPAGE);

		//list 바인딩
		request.setAttribute("list", list);
		request.setAttribute("pageMenu", pageMenu);

		session = request.getSession();

		//세션을 비운다	
		session.removeAttribute("s");

		return Common.PATH + "board_list.jsp";
	}

	//새 글 작성을 위한 폼으로 화면전환
	@RequestMapping("/insert_form.do")
	public String insert_form() {
		return Common.PATH + "board_write.jsp";
	}

	//새글 작성
	@RequestMapping("/insert.do")
	public String insert( BoardVO vo ) {

		vo.setIp( request.getRemoteAddr() );

		board_dao.insert(vo);

		return "redirect:list.do"; 

	}

	//게시글 상세보기
	@RequestMapping("/view.do")
	public String view( Model model, int idx ) {
		//view.do?idx=10
		BoardVO vo = board_dao.selectOne(idx); //상세보기 페이지를 위한 객체검색

		//폭발적인 조회수 증가를 방지하기 위해 session저장공간을 사용
		session = request.getSession();

		String show = (String)session.getAttribute("s");
		if( show == null ) {
			//조회수 증가
			int res = board_dao.update_readhit(idx);
			session.setAttribute("s", "check");

		}

		model.addAttribute("vo", vo);//바인딩
		return Common.PATH + "board_view.jsp";
	}

	//댓글작성 페이지로 전환
	@RequestMapping("/reply_form.do")
	public String reply_form() {
		return Common.PATH + "board_reply.jsp";
	}

	//댓글처리
	@RequestMapping("/reply.do")
	public String reply( BoardVO vo, Integer page ) {

		int nowPage = 1;

		if( page != null ) {
			nowPage = page;
		}

		//댓글이 달릴 게시물
		BoardVO base_vo = board_dao.selectOne(vo.getIdx());

		//기준글의 step값보다 큰 값을 가지고 있는 모든 게시물을 step+1처리
		board_dao.update_step( base_vo );

		vo.setIp( request.getRemoteAddr() );

		//댓글이 들어갈 위치 선정
		vo.setRef( base_vo.getRef() );
		vo.setStep( base_vo.getStep() + 1);
		vo.setDepth( base_vo.getDepth() + 1);

		//댓글을 DB에 insert
		board_dao.reply(vo);
		return "redirect:list.do?page="+nowPage;

	}

	//글 삭제(된 것 처럼 업데이트)
	@RequestMapping("/del.do")
	@ResponseBody
	public String delete( int idx ) {

		//idx에 해당하는 게시글 한 건 조회
		BoardVO baseVO = board_dao.selectOne(idx);

		baseVO.setSubject("삭제된 게시글 입니다");
		baseVO.setName("unknown");

		//삭제 업데이트
		int res = board_dao.del_update(baseVO);

		String result = "no";
		if( res == 1 ) {
			result = "yes";
		}

		return result;//no 또는 yes를 콜백메서드로 전달
	}

}






























