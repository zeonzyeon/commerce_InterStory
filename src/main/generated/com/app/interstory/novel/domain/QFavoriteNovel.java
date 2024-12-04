package com.app.interstory.novel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFavoriteNovel is a Querydsl query type for FavoriteNovel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoriteNovel extends EntityPathBase<FavoriteNovel> {

    private static final long serialVersionUID = 2063647203L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFavoriteNovel favoriteNovel = new QFavoriteNovel("favoriteNovel");

    public final NumberPath<Long> favoriteNovelId = createNumber("favoriteNovelId", Long.class);

    public final QNovel novel;

    public final com.app.interstory.user.domain.entity.QUser user;

    public QFavoriteNovel(String variable) {
        this(FavoriteNovel.class, forVariable(variable), INITS);
    }

    public QFavoriteNovel(Path<? extends FavoriteNovel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFavoriteNovel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFavoriteNovel(PathMetadata metadata, PathInits inits) {
        this(FavoriteNovel.class, metadata, inits);
    }

    public QFavoriteNovel(Class<? extends FavoriteNovel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.novel = inits.isInitialized("novel") ? new QNovel(forProperty("novel"), inits.get("novel")) : null;
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user")) : null;
    }

}

