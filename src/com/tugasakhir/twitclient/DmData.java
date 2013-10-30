package com.tugasakhir.twitclient;

public class DmData {

	/**
	 * Sender ID
	 */
	private long senderID;
	private String senderName;
	
	public DmData(long idPengirim, String namaPengirim){
		senderID = idPengirim;
		senderName = namaPengirim;
	}
	
	/*
	 * Get ID pengirim
	 */
	public long getSenderId(){
		return senderID;
	}
	
	/*
	 * Get ScreenName pengirim
	 */
	public String getSenderName(){
		return senderName;
	}
	
}
