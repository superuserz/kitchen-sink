package com.kitchensink.user.requests;

/**
 * @author manmeetdevgun
 * 
 *         The Class MemberCriteriaRequest.
 */
public class MemberCriteriaRequest {

	/** The page. */
	private int page;

	/** The size. */
	private int size;

	/** The sort by. */
	private String sortBy;

	/** The direction. */
	private String direction;

	/** The name. */
	private String name;

	/** The email. */
	private String email;

	/** The role. */
	private String role;

	public MemberCriteriaRequest() {
		super();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "MemberCriteriaRequest [page=" + page + ", size=" + size + ", sortBy=" + sortBy + ", direction="
				+ direction + ", name=" + name + ", email=" + email + ", role=" + role + "]";
	}
}
