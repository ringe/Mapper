package no.rin.mapper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.maps.GeoPoint;

public class UserData {
	
	private GeoPoint p;
	private String rebus, username;
	private int contest_id;
	private List<UserData> userdata;
	private UserAdapter db;
	
	public UserData() {}
	
	public UserData(UserAdapter userDB) {
		db = userDB;
		userdata = db.selectLast();
	
		if(userdata.isEmpty()) {
			Register b = (Register) new Register().execute();
			try {
				username = b.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			UserData u = userdata.get(0);
			username = u.getUsername();
			db.insertUser(username);
		}
		db.close();
	}
	
	public GeoPoint getP() {
		return p;
	}
	public void setP(GeoPoint p) {
		this.p = p;
	}
	public String getRebus() {
		return rebus;
	}
	public void setRebus(String rebus) {
		this.rebus = rebus;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getContest_id() {
		return contest_id;
	}
	public void setContest_id(int contest_id) {
		this.contest_id = contest_id;
	}

}
