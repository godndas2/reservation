package com.halfdev.pj.book;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookDAO {

	@Autowired
	private SqlSession sqlSession;
	
	public void bookinsert(BookVO bv) {
		sqlSession.insert("book.bookinsert",bv);
	}
	public List<BookVO> mybookselect(String id){
		return sqlSession.selectList("book.mybookselect",id);
	}
	public void mybookdelete(String seq) {
		sqlSession.delete("book.mybookdelete",seq);
	}
}
