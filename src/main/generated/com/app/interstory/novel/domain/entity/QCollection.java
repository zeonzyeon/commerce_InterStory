package com.app.interstory.novel.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCollection is a Querydsl query type for Collection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCollection extends EntityPathBase<Collection> {

    private static final long serialVersionUID = -452582356L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCollection collection = new QCollection("collection");

    public final NumberPath<Long> collectionId = createNumber("collectionId", Long.class);

    public final QEpisode episode;

    public final com.app.interstory.user.domain.entity.QUser user;

    public QCollection(String variable) {
        this(Collection.class, forVariable(variable), INITS);
    }

    public QCollection(Path<? extends Collection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCollection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCollection(PathMetadata metadata, PathInits inits) {
        this(Collection.class, metadata, inits);
    }

    public QCollection(Class<? extends Collection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.episode = inits.isInitialized("episode") ? new QEpisode(forProperty("episode"), inits.get("episode")) : null;
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user")) : null;
    }

}

