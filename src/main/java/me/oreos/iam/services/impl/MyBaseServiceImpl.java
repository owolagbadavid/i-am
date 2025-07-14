package me.oreos.iam.services.impl;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.wakanda.framework.exception.BaseException;
import org.wakanda.framework.repository.BaseRepository;
import org.wakanda.framework.service.BaseServiceImpl;

import me.oreos.iam.entities.MyBaseEntity;
import me.oreos.iam.services.MyBaseService;

public class MyBaseServiceImpl<T extends MyBaseEntity<ID>, ID extends Serializable> extends BaseServiceImpl<T, ID>
        implements MyBaseService<T, ID> {

    protected MyBaseServiceImpl(BaseRepository<T, ID> baseRepository) {
        super(baseRepository);
    }

    @Override
    public T delete(ID entityId) throws BaseException {
        T alreadyPresentEntity = this.preDelete(entityId);
        alreadyPresentEntity.setIsActive(Boolean.FALSE);
        alreadyPresentEntity.setDeletedOn(DateTime.now());
        return this.postDelete(this.update(alreadyPresentEntity, entityId));
    }
}
