package com.app.interstory.novel.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNovel is a Querydsl query type for Novel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNovel extends EntityPathBase<Novel> {

    private static final long serialVersionUID = -1121574930L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNovel novel = new QNovel("novel");

    public final StringPath description = createString("description");

    public final DateTimePath<java.sql.Timestamp> episodeUpdatedAt = createDateTime("episodeUpdatedAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> favoriteCount = createNumber("favoriteCount", Integer.class);

    public final BooleanPath isFree = createBoolean("isFree");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Long> novelId = createNumber("novelId", Long.class);

    public final StringPath plan = createString("plan");

    public final DateTimePath<java.sql.Timestamp> publishedAt = createDateTime("publishedAt", java.sql.Timestamp.class);

    public final EnumPath<com.app.interstory.novel.domain.enumtypes.NovelStatus> status = createEnum("status", com.app.interstory.novel.domain.enumtypes.NovelStatus.class);

    public final EnumPath<com.app.interstory.novel.domain.enumtypes.MainTag> tag = createEnum("tag", com.app.interstory.novel.domain.enumtypes.MainTag.class);

    public final StringPath thumbnailRenamedFilename = createString("thumbnailRenamedFilename");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final com.app.interstory.user.domain.entity.QUser user;

    public QNovel(String variable) {
        this(Novel.class, forVariable(variable), INITS);
    }

    public QNovel(Path<? extends Novel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNovel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNovel(PathMetadata metadata, PathInits inits) {
        this(Novel.class, metadata, inits);
    }

    public QNovel(Class<? extends Novel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

