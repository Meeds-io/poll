package org.exoplatform.poll.dao;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.poll.entity.PollOptionEntity;

public class PollOptionDAO extends GenericDAOJPAImpl<PollOptionEntity, Long> {
  @Override
  @ExoTransactional
  public PollOptionEntity create(PollOptionEntity pollOptionEntity) {
    return super.create(pollOptionEntity);
  }
}
