package com.halfdev.pj.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentVO {

	private String comment_seq;
	private String parent_seq;
	private String writer;
	private String regDate;
	private String content;
	private String modified;
	private String up;
	
	public String getComment_seq() {
		return comment_seq;
	}
	public void setComment_seq(String comment_seq) {
		this.comment_seq = comment_seq;
	}
	public String getParent_seq() {
		return parent_seq;
	}
	public void setParent_seq(String parent_seq) {
		this.parent_seq = parent_seq;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	
	@Override
	public String toString() {
		return "CommentVO [comment_seq=" + comment_seq + ", parent_seq=" + parent_seq + ", writer=" + writer
				+ ", regDate=" + regDate + ", content=" + content + ", modified=" + modified + ", up=" + up + "]";
	}
	
	

	
}
