package org.exoplatform.poll.dao;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.poll.entity.PollEntity;

public class PollDAO extends GenericDAOJPAImpl<PollEntity, Long> {
  @Override
  @ExoTransactional
  public PollEntity create(PollEntity entity) {
    return super.create(entity);
  }

}
