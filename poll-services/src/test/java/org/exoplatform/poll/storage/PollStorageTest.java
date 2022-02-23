package org.exoplatform.poll.storage;

import org.exoplatform.poll.dao.PollDAO;
import org.exoplatform.poll.dao.PollOptionDAO;
import org.exoplatform.poll.entity.PollEntity;
import org.exoplatform.poll.entity.PollOptionEntity;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.utils.EntityMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.xml.*", "org.xml.*" })
public class PollStorageTest {
  private PollStorage pollStorage;

  private PollDAO     pollDAO;
  private PollOptionDAO     pollOptionDAO;

  @Before
  public void setUp() throws Exception { // NOSONAR
    pollDAO = mock(PollDAO.class);
    pollOptionDAO = mock(PollOptionDAO.class);
    pollStorage = new PollStorage(pollDAO, pollOptionDAO);
  }

  @PrepareForTest({ EntityMapper.class })
  @Test
  public void testCreatePoll() throws Exception { // NOSONAR
      Date startDate = new Date(System.currentTimeMillis());
      Date endDate = new Date(System.currentTimeMillis() + 1);
      Poll poll = new Poll();
      poll.setQuestion("q1");
      poll.setStartDate(startDate);
      poll.setEndDate(endDate);
      poll.setCreatorId(1L);

      PollOption pollOption = new PollOption();
      pollOption.setPollId(poll.getId());
      pollOption.setPollOption("pollOption");

      PollEntity pollEntity = new PollEntity();
      pollEntity.setPollQuestion("q1");
      pollEntity.setStart_date(startDate);
      pollEntity.setEnd_date(endDate);
      pollEntity.setIdentityId(1L);

      PollOptionEntity pollOptionEntity = new PollOptionEntity();
      pollOptionEntity.setPollId(pollEntity.getId());
      pollOptionEntity.setPollOption(pollOption.getPollOption());

      when(pollDAO.create(anyObject())).thenReturn(pollEntity);
      when(pollOptionDAO.create(anyObject())).thenReturn(pollOptionEntity);
      PowerMockito.mockStatic(EntityMapper.class);
      when(EntityMapper.toEntity(poll)).thenReturn(pollEntity);
      when(EntityMapper.fromEntity(pollEntity)).thenReturn(poll);
      when(EntityMapper.optionToEntity(pollOption)).thenReturn(pollOptionEntity);
      when(EntityMapper.optionFromEntity(pollOptionEntity)).thenReturn(pollOption);
      PollOption pollOption1 = pollStorage.createPollOption(pollOption);

      Poll poll2 = pollStorage.createPoll(poll);


  }
}
