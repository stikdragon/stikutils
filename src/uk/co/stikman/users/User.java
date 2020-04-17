package uk.co.stikman.users;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import uk.co.stikman.utils.RandomString;
import uk.co.stikman.utils.Utils;

public abstract class User {
	private String				id;
	private String				name;
	private String				passwordHash;
	private String				passwordSalt;
	private boolean				admin;
	private Map<String, String>	attributes	= new HashMap<>();
	private boolean				enabled		= true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getId() {
		return id;
	}

	public User(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setNewPassword(String pass) {
		RandomString rs = new RandomString(16);
		this.passwordSalt = rs.nextString();
		this.passwordHash = Utils.hashPassword(pass, passwordSalt);
	}

	public boolean testPassword(String pass) {
		return Utils.hashPassword(pass, passwordSalt).equals(this.passwordHash);
	}

	public void setPasswordAndHash(String hash, String salt) {
		this.passwordHash = hash;
		this.passwordSalt = salt;
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Save any extra properties you add in descendant types here
	 * 
	 * @param output
	 * @throws IOException 
	 */
	public abstract void saveTo(OutputStream output) throws IOException;

	/**
	 * Read any extra properties you add in descendant types here
	 * 
	 * @param input
	 */
	public abstract void readFrom(InputStream input) throws IOException;
}
