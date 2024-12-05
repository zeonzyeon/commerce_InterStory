package com.app.interstory.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = 1792634031L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final StringPath code = createString("code");

    public final NumberPath<Long> couponId = createNumber("couponId", Long.class);

    public final NumberPath<Long> discount = createNumber("discount", Long.class);

    public final DateTimePath<java.sql.Timestamp> expiredAt = createDateTime("expiredAt", java.sql.Timestamp.class);

    public final NumberPath<Long> minimum = createNumber("minimum", Long.class);

    public final StringPath name = createString("name");

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

