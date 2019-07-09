package com.halfdev.pj.room;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomDAO {

	@Autowired
	private SqlSession sqlSession;
	
	public void roomset(RoomVO rv) {
		sqlSession.selectOne("room.roomset",rv);
	}
	public RoomVO roomcheck(RoomVO rv) {
		return sqlSession.selectOne("room.roomcheck",rv);
	}
	public RoomVO stayroomcheck(RoomVO rv) {
		return sqlSession.selectOne("room.stayroomcheck",rv);
	}
	public RoomVO roomupdate(RoomVO rv) {
		return sqlSession.selectOne("room.roomupdate",rv);
	}
	public List<RoomVO> remainingrooms(String room_type){
		return sqlSession.selectList("room.remainingrooms",room_type);
	}
	public void roomchange(RoomVO rv) {
		sqlSession.selectList("room.roomchange",rv);
	}
}
