package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

/**
 * MYesnoTxtId generated by hbm2java
 */
public class MYesnoTxtId implements java.io.Serializable {

	private byte yesId;
	private short sprId;

	public MYesnoTxtId() {
	}

	public MYesnoTxtId(byte yesId, short sprId) {
		this.yesId = yesId;
		this.sprId = sprId;
	}

	public byte getYesId() {
		return this.yesId;
	}

	public void setYesId(byte yesId) {
		this.yesId = yesId;
	}

	public short getSprId() {
		return this.sprId;
	}

	public void setSprId(short sprId) {
		this.sprId = sprId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof MYesnoTxtId))
			return false;
		MYesnoTxtId castOther = (MYesnoTxtId) other;

		return (this.getYesId() == castOther.getYesId())
				&& (this.getSprId() == castOther.getSprId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getYesId();
		result = 37 * result + this.getSprId();
		return result;
	}

}