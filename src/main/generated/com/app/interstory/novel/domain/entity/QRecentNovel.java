package com.app.interstory.novel.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecentNovel is a Querydsl query type for RecentNovel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecentNovel extends EntityPathBase<RecentNovel> {

    private static final long serialVersionUID = -742646573L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecentNovel recentNovel = new QRecentNovel("recentNovel");

    public final QEpisode episode;

    public final QNovel novel;

    public final NumberPath<Long> recentNovelId = createNumber("recentNovelId", Long.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final com.app.interstory.user.domain.entity.QUser user;

    public QRecentNovel(String variable) {
        this(RecentNovel.class, forVariable(variable), INITS);
    }

    public QRecentNovel(Path<? extends RecentNovel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecentNovel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecentNovel(PathMetadata metadata, PathInits inits) {
        this(RecentNovel.class, metadata, inits);
    }

    public QRecentNovel(Class<? extends RecentNovel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.episode = inits.isInitialized("episode") ? new QEpisode(forProperty("episode"), inits.get("episode")) : null;
        this.novel = inits.isInitialized("novel") ? new QNovel(forProperty("novel"), inits.get("novel")) : null;
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

