package uk.co.stikman.users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.stikman.utils.RandomString;

/**
 * This is thread-safe, you don't need to synchronize on anything
 * 
 * @author stik
 *
 * @param <T>
 *            a type extending {@link User} , you'll need to implement
 *            {@link #createUserInstance(String, String)} as a factory method to
 *            return them
 */
public abstract class Users<T extends User> implements Iterable<T> {
	public static class TokenEntry<T> {
		private String				token;
		private T					user;
		private long				lastTime;
		private Map<String, Object>	data	= new HashMap<>();

		public String getToken() {
			return token;
		}

		public T getUser() {
			return user;
		}

		public long getLastTime() {
			return lastTime;
		}

		public TokenEntry(String token, T user) {
			super();
			this.token = token;
			this.user = user;
			refresh();
		}

		public void refresh() {
			this.lastTime = System.currentTimeMillis();
		}

		public long getAge() {
			return System.currentTimeMillis() - lastTime;
		}

		public Map<String, Object> getData() {
			return data;
		}

	}

	private static final long			MAX_AGE		= 1000 * 60 * 15;	// 15 minutes

	private Map<String, T>				users		= new HashMap<>();
	private Map<String, TokenEntry<T>>	tokens		= new HashMap<>();
	private long						maxTokenAge	= MAX_AGE;

	@Override
	public Iterator<T> iterator() {
		return users.values().iterator();
	}

	public void load(String json) throws JSONException {
		synchronized (this) {
			JSONObject jo = new JSONObject(json);
			JSONArray arr = jo.getJSONArray("users");
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject ju = arr.getJSONObject(i);
				T u = createUserInstance(ju.getString("uid"), ju.getString("name"));
				if (users.containsKey(u.getName()))
					throw new RuntimeException("Duplicate user: " + u.getName());
				if (ju.has("password"))
					u.setNewPassword(ju.getString("password"));
				else
					u.setPasswordAndHash(ju.getString("passhash"), ju.getString("passsalt"));
				u.setEnabled(ju.optBoolean("enabled", true));
				u.setAdmin(ju.optBoolean("admin", false));
				JSONObject attrs = ju.optJSONObject("attributes");
				if (attrs != null) {
					@SuppressWarnings("unchecked")
					Iterator<String> iter = attrs.keys();
					while (iter.hasNext()) {
						String k = iter.next();
						u.getAttributes().put(k, attrs.getString(k));
					}
				}

				//
				// get user-defined stuff
				//
				String b64 = ju.optString("data", null);
				if (b64 != null) {
					byte[] bytes = Base64.getDecoder().decode(b64);
					try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
						u.readFrom(bais);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				users.put(u.getName(), u);
			}
		}
	}

	protected abstract T createUserInstance(String uid, String name);

	public String save() throws JSONException {
		synchronized (this) {
			JSONObject jo = new JSONObject();
			JSONArray arr = new JSONArray();
			for (T u : users.values()) {
				JSONObject j = new JSONObject();
				j.put("uid", u.getId());
				j.put("name", u.getName());
				j.put("passhash", u.getPasswordHash());
				j.put("passsalt", u.getPasswordSalt());
				j.put("admin", u.isAdmin());
				j.put("enabled", u.isEnabled());
				JSONObject attrs = new JSONObject();
				for (Entry<String, String> e : u.getAttributes().entrySet())
					attrs.put(e.getKey(), e.getValue());
				j.put("attributes", attrs);

				//
				// Stream any user-defined stuff as binary and base64 it
				//
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					u.saveTo(baos);
					baos.flush();
					if (baos.size() > 0)
						j.put("data", new String(Base64.getEncoder().encode(baos.toByteArray()), "UTF-8"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				arr.put(j);
			}
			jo.put("users", arr);
			return jo.toString(4);
		}
	}

	public T find(String name) {
		synchronized (this) {
			return users.get(name);
		}
	}

	public T create(String user, String pwd, boolean admin) {
		synchronized (this) {
			if (find(user) != null)
				throw new IllegalStateException("User " + user + " already exists");
			T u = createUserInstance(RandomString.get(16), user);
			u.setNewPassword(pwd);
			u.setAdmin(admin);
			u.setEnabled(true);
			users.put(user, u);
			return u;
		}
	}

	public T findByToken(String token) {
		TokenEntry<T> te;
		synchronized (this) {
			te = tokens.get(token);
		}
		if (te == null)
			return null;
		return te.getUser();
	}

	public TokenEntry<T> createToken(T user) {
		String s = RandomString.get(16);
		TokenEntry<T> te = new TokenEntry<>(s, user);
		synchronized (this) {
			tokens.put(s, te);
		}
		return te;
	}

	/**
	 * Can return <code>null</code>
	 * 
	 * @param token
	 * @return
	 */
	public TokenEntry<T> findToken(String token) {
		synchronized (this) {
			return tokens.get(token);
		}
	}

	/**
	 * Throes {@link NoSuchElementException} if not found
	 * 
	 * @param token
	 * @return
	 */
	public TokenEntry<T> getToken(String token) {
		synchronized (this) {
			TokenEntry<T> te = tokens.get(token);
			if (te == null)
				throw new NoSuchElementException(token);
			return te;
		}
	}

	/**
	 * Removes any old tokens
	 */
	public void pruneTokens() {
		synchronized (this) {
			for (Iterator<Map.Entry<String, TokenEntry<T>>> it = tokens.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, TokenEntry<T>> entry = it.next();
				if (entry.getValue().getAge() > maxTokenAge)
					it.remove();
			}
		}
	}

	/**
	 * Returns <code>null</code> if not found
	 * 
	 * @param id
	 * @return
	 */
	public T findById(String id) {
		synchronized (this) {
			for (T u : users.values())
				if (u.getId().equals(id))
					return u;
		}
		return null;
	}

	/**
	 * In milliseconds
	 * 
	 * @return
	 */
	public long getMaxTokenAge() {
		return maxTokenAge;
	}

	/**
	 * In milliseconds
	 * 
	 * @param maxTokenAge
	 */
	public void setMaxTokenAge(long maxTokenAge) {
		this.maxTokenAge = maxTokenAge;
	}

	/**
	 * Returns <code>true</code> if the password matches
	 * 
	 * @param u
	 * @param password
	 * @return
	 */
	public boolean tryLogin(T u, String password) {
		if (u == null)
			return false;
		if (!u.isEnabled())
			return false;
		return u.testPassword(password);
	}

	/**
	 * Returns <code>true</code> if it deleted anything
	 * 
	 * @param u
	 * @return
	 */
	public boolean delete(T u) {
		synchronized (this) {
			T b = users.remove(u.getName());

			//
			// Remove any tokens belonging to the user
			//
			for (Iterator<Map.Entry<String, TokenEntry<T>>> it = tokens.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, TokenEntry<T>> entry = it.next();
				if (entry.getValue().getUser().equals(u))
					it.remove();
			}
			return b != null;
		}
	}

	public boolean isEmpty() {
		synchronized (this) {
			return users.isEmpty();
		}
	}
}
