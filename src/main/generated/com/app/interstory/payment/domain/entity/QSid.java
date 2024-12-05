package com.app.interstory.payment.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSid is a Querydsl query type for Sid
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSid extends EntityPathBase<Sid> {

    private static final long serialVersionUID = -975277302L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSid sid1 = new QSid("sid1");

    public final StringPath sid = createString("sid");

    public final NumberPath<Long> sidId = createNumber("sidId", Long.class);

    public final com.app.interstory.user.domain.entity.QUser user;

    public QSid(String variable) {
        this(Sid.class, forVariable(variable), INITS);
    }

    public QSid(Path<? extends Sid> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSid(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSid(PathMetadata metadata, PathInits inits) {
        this(Sid.class, metadata, inits);
    }

    public QSid(Class<? extends Sid> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.app.interstory.user.domain.entity.QUser(forProperty("user")) : null;
    }

}

