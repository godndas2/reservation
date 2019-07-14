package com.halfdev.pj.board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BoardDAO {

	@Autowired SqlSession sqlSession;
	@Autowired ServletContext context;
	
	public List<BoardVO> boardList(int i) {
		return sqlSession.selectList("board.boardList", i);
	}
	
	//글 수정에서 파일 추가
	public void addfiles(List<MultipartFile> mf,String folder) throws IllegalStateException, IOException {
		String path = context.getRealPath("/WEB-INF/file/"+folder);
		System.out.println("path>>" + path);
		String filename;
		
		for(int i=0;i<mf.size();i++) {
			filename=new String(mf.get(i).getOriginalFilename().getBytes("8859_1"),"utf-8");
			filename=filename.replaceAll("#", "");
			if(filename.length()==0)
			{
				filename=""+i;
			}
			mf.get(i).transferTo(new File(path+"/"+filename));
		
		}
		
	}
	//글 작성 시 파일 업로드
	public String uploadfiles(List<MultipartFile> mf,String id) throws IllegalStateException, IOException {
	   
		String path=context.getRealPath("/WEB-INF/file/");
		Date date=new Date();

		String filename;
		String path2=id+date.getTime();
		//폴더생성
		File parent=new File(path);
		File desti = new File(parent,path2);
		 if(!desti.exists()){
			desti.mkdir();
		
		 }
		 path=path+path2;
	
		//파일업로드
		for(int i=0;i<mf.size();i++) {
			filename=mf.get(i).getOriginalFilename();
			filename=filename.replaceAll("#", "");
			if(filename.length()==0)
			{
				filename=""+i;
			}
			mf.get(i).transferTo(new File(path+"/"+filename));
		
		}
		return path2;
		
	}
	public List<String> fileread(String folder){
		String path=context.getRealPath("/WEB-INF/file/"+folder);
	
		File dirFile=new File(path);
		File[] fileList=dirFile.listFiles();
		List<String> list = new ArrayList<String>();
		for(File tempFile : fileList) {
			  if(tempFile.isFile()) {
				  list.add(tempFile.getName());
			  }
			}
		return list;
	}
	public void updatefile(String[] list,String folder) {
		String path=context.getRealPath("/WEB-INF/file/"+folder);
		File dirFile=new File(path);
		File[] fileList=dirFile.listFiles();
		for(int i=0;i<list.length;i++) {
			for(File tempFile : fileList) {
				if(tempFile.isFile()) {
				
					if(tempFile.getName().equals(list[i].toString())){
				
						tempFile.delete();
					}
				}
			}
		}
	
	}
	public void filedelete (String folder) {
		String path= context.getRealPath("/WEB-INF/file/"+folder);
		
		File dirFile=new File(path);
		File[] fileList=dirFile.listFiles();
		for(File tempFile : fileList) {
			  if(tempFile.isFile()) {
				  tempFile.delete();
			  }
		}
		dirFile.delete();
	}
	public void boardinsert(BoardVO bv) {
		sqlSession.insert("board.boardInsert",bv);
	}
	public BoardVO selectread(BoardVO bv) {
		sqlSession.update("board.updateHitCnt",bv);
		return sqlSession.selectOne("board.boardSelectRead",bv);
	}
	public int boardCount() {
		return sqlSession.selectOne("board.boardCount");
	}
	public void boarddelete(BoardVO bv) {
		sqlSession.delete("board.boardDelete",bv);
	}
	public void boardupdate(BoardVO bv) {
		sqlSession.update("board.boardUpdate",bv);
	}
	public String securitycheck(BoardVO bv) {
		return sqlSession.selectOne("board.securitycheck",bv);
	}
}
