package com.app.interstory.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSocial is a Querydsl query type for Social
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSocial extends EntityPathBase<Social> {

    private static final long serialVersionUID = -2044810250L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSocial social = new QSocial("social");

    public final StringPath clientId = createString("clientId");

    public final EnumPath<com.app.interstory.user.domain.enumtypes.Provider> provider = createEnum("provider", com.app.interstory.user.domain.enumtypes.Provider.class);

    public final NumberPath<Long> socialId = createNumber("socialId", Long.class);

    public final QUser user;

    public QSocial(String variable) {
        this(Social.class, forVariable(variable), INITS);
    }

    public QSocial(Path<? extends Social> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSocial(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSocial(PathMetadata metadata, PathInits inits) {
        this(Social.class, metadata, inits);
    }

    public QSocial(Class<? extends Social> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

