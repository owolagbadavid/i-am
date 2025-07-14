package me.oreos.iam.services;


import java.io.Serializable;

import org.wakanda.framework.entity.BaseEntity;
import org.wakanda.framework.service.BaseService;

public interface MyBaseService<T extends BaseEntity<ID>, ID extends Serializable> extends BaseService<T, ID> {
}
