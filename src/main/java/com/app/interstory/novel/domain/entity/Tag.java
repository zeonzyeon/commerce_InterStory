package com.app.interstory.novel.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long tagId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;

	@Column(name = "tag", nullable = false)
	private String tag;

	public Tag(Novel novel,  String tag) {
		this.novel = novel;
		this.tag = tag;
	}
}
