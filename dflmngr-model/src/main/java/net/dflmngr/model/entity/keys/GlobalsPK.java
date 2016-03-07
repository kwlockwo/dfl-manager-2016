package net.dflmngr.model.entity.keys;

import java.io.Serializable;

public class GlobalsPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String code;
	private String groupCode;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getGroupCode() {
		return groupCode;
	}
	
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	@Override
	public String toString() {
		return "GlobalsPK [code=" + code + ", groupCode=" + groupCode + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((groupCode == null) ? 0 : groupCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GlobalsPK other = (GlobalsPK) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (groupCode == null) {
			if (other.groupCode != null) {
				return false;
			}
		} else if (!groupCode.equals(other.groupCode)) {
			return false;
		}
		return true;
	}
}
