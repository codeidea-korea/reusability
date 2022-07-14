package com.codeidea.login.api.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_REFRESH_TOKEN")
public class UserRefreshToken {

	@JsonIgnore
	@Id
	@Column(name = "REFRESH_TOKEN_SEQ")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long refreshTokenSeq;

	@Column(name = "USER_ID", length = 64, unique = true)
	@NotNull
	@Size(max = 64)
	private String userId;

	@Column(name = "REFRESH_TOKEN", length = 256)
	@NotNull
	@Size(max = 256)
	private String refreshToken;

	public UserRefreshToken(@NotNull @Size(max = 64) String userId, @NotNull @Size(max = 256) String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
}
