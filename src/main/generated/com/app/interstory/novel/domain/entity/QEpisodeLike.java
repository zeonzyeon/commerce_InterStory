package com.app.interstory.novel.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEpisodeLike is a Querydsl query type for EpisodeLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEpisodeLike extends EntityPathBase<EpisodeLike> {

    private static final long serialVersionUID = 100575332L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEpisodeLike episodeLike = new QEpisodeLike("episodeLike");

    public final QEpisode episode;

    public final NumberPath<Long> episodeLikeId = createNumber("episodeLikeId", Long.class);

    public final com.app.interstory.user.domain.entity.QUser user;

    public QEpisodeLike(String variable) {
        this(EpisodeLike.class, forVariable(variable), INITS);
    }

    public QEpisodeLike(Path<? extends EpisodeLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEpisodeLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEpisodeLike(PathMetadata metadata, PathInits inits) {
        this(EpisodeLike.class, metadata, inits);
    }

    public QEpisodeLike(Class<? extends EpisodeLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.episode = inits.isInitialized("episode") ? new QEpisode(forProperty("episode"), inits.get("episode")) : null;
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

