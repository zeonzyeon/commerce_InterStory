package com.app.interstory.novel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEpisode is a Querydsl query type for Episode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEpisode extends EntityPathBase<Episode> {

    private static final long serialVersionUID = -821516258L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEpisode episode = new QEpisode("episode");

    public final StringPath content = createString("content");

    public final NumberPath<Long> episodeId = createNumber("episodeId", Long.class);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final QNovel novel;

    public final DateTimePath<java.sql.Timestamp> publishedAt = createDateTime("publishedAt", java.sql.Timestamp.class);

    public final BooleanPath status = createBoolean("status");

    public final StringPath thumbnailRenamedFilename = createString("thumbnailRenamedFilename");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QEpisode(String variable) {
        this(Episode.class, forVariable(variable), INITS);
    }

    public QEpisode(Path<? extends Episode> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEpisode(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEpisode(PathMetadata metadata, PathInits inits) {
        this(Episode.class, metadata, inits);
    }

    public QEpisode(Class<? extends Episode> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.novel = inits.isInitialized("novel") ? new QNovel(forProperty("novel"), inits.get("novel")) : null;
    }

}

