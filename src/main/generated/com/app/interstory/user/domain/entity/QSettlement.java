package com.app.interstory.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSettlement is a Querydsl query type for Settlement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSettlement extends EntityPathBase<Settlement> {

    private static final long serialVersionUID = 840806706L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSettlement settlement = new QSettlement("settlement");

    public final StringPath accountNumber = createString("accountNumber");

    public final NumberPath<Long> fee = createNumber("fee", Long.class);

    public final NumberPath<Long> settlementId = createNumber("settlementId", Long.class);

    public final QUser user;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QSettlement(String variable) {
        this(Settlement.class, forVariable(variable), INITS);
    }

    public QSettlement(Path<? extends Settlement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSettlement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSettlement(PathMetadata metadata, PathInits inits) {
        this(Settlement.class, metadata, inits);
    }

    public QSettlement(Class<? extends Settlement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

