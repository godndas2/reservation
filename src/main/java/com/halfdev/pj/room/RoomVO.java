package com.halfdev.pj.room;

import org.springframework.stereotype.Component;

@Component
public class RoomVO {
	private int roomSeq;
	private String roomType;
	private String roomDate;
	private int roomStay;
	
	public int getRoomSeq() {
		return roomSeq;
	}
	public void setRoomSeq(int roomSeq) {
		this.roomSeq = roomSeq;
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public String getRoomDate() {
		return roomDate;
	}
	public void setRoomDate(String roomDate) {
		this.roomDate = roomDate;
	}
	public int getRoomStay() {
		return roomStay;
	}
	public void setRoomStay(int roomStay) {
		this.roomStay = roomStay;
	}
	
	@Override
	public String toString() {
		return "RoomVO [roomSeq=" + roomSeq + ", roomType=" + roomType + ", roomDate=" + roomDate + ", roomStay="
				+ roomStay + "]";
	}
	
	
	
	
}
