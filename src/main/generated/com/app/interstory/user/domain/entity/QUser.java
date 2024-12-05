package com.app.interstory.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1883967124L;

    public static final QUser user = new QUser("user");

    public final BooleanPath autoPayment = createBoolean("autoPayment");

    public final StringPath createdAt = createString("createdAt");

    public final StringPath email = createString("email");

    public final BooleanPath isActivity = createBoolean("isActivity");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final StringPath profileRenamedFilename = createString("profileRenamedFilename");

    public final StringPath profileUrl = createString("profileUrl");

    public final EnumPath<com.app.interstory.user.domain.enumtypes.Roles> role = createEnum("role", com.app.interstory.user.domain.enumtypes.Roles.class);

    public final BooleanPath subscribe = createBoolean("subscribe");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

