package com.app.interstory.user.dto.request;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {

	private File file;
	private String renamedFileName;

}
