package com.halfdev.pj.comment;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentDAO {

	@Autowired
	private SqlSession sqlSession;
	
	public void commentinsert(CommentVO cv) {
		sqlSession.selectList("comment.commentinsert",cv);
	}
	public List<CommentVO> commentlist(CommentVO cv){
		return sqlSession.selectList("comment.commentlist",cv);
	}
	public void commentupdate(CommentVO cv) {
		sqlSession.update("comment.commentupdate",cv);
	}
	public CommentVO commentselect(CommentVO cv) {
		return sqlSession.selectOne("comment.commentselect",cv);
	}
	@Transactional
	public void commentdelete(CommentVO cv) {
		sqlSession.delete("comment.commentdelete",cv);
	}
}
