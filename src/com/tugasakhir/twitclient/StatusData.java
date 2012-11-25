package com.tugasakhir.twitclient;

/**
 * StatusData class models the data for a single tweet in the timeline
 * - the class stores the tweet ID and user screen name for use within replies and retweets
 */
public class StatusData {

		/**tweet ID*/
		private long tweetID;
		/**user screen name of tweeter*/
		private String tweetUser;
		
		/**
		 * Constructor receives ID and user name
		 * @param ID
		 * @param screenName
		 */
		public StatusData(long ID, String screenName) {
				//instantiate variables
			tweetID=ID;
			tweetUser=screenName;
		}
		
		/**
		 * Get the ID of the tweet
		 * @return tweetID as a long
		 */
		public long getID() {return tweetID;}
		
		/**
		 * Get the user screen name for the tweet
		 * @return tweetUser as a String
		 */
		public String getUser() {return tweetUser;}
	
}
