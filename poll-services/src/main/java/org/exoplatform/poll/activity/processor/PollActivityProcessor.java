package org.exoplatform.poll.activity.processor;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.poll.model.Poll;
import org.exoplatform.poll.model.PollOption;
import org.exoplatform.poll.rest.model.PollRestEntity;
import org.exoplatform.poll.service.PollService;
import org.exoplatform.poll.utils.PollUtils;
import org.exoplatform.poll.utils.RestEntityBuilder;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

public class PollActivityProcessor extends BaseActivityProcessorPlugin {

  private PollService      pollService;

  private static final Log LOG = ExoLogger.getLogger(PollActivityProcessor.class);

  public PollActivityProcessor(PollService pollService, InitParams initParams) {
    super(initParams);
    this.pollService = pollService;
  }

  @Override
  public void processActivity(ExoSocialActivity activity) {
    if (activity.isComment() || activity.getType() == null || !activity.getTemplateParams().containsKey(PollUtils.POLL_ID)) {
      return;
    }
    if (activity.getLinkedProcessedEntities() == null) {
      activity.setLinkedProcessedEntities(new HashMap<>());
    }
    PollRestEntity pollRestEntity = (PollRestEntity) activity.getLinkedProcessedEntities().get(PollUtils.POLL_ACTIVITY_TYPE);
    
    if (pollRestEntity == null) {
      Identity currentIdentity = ConversationState.getCurrent().getIdentity();
      String pollId = activity.getTemplateParams().get(PollUtils.POLL_ID);
      try {
        Poll poll = pollService.getPollById(Long.parseLong(pollId), currentIdentity);
        List<PollOption> pollOptions = pollService.getPollOptionsById(Long.parseLong(pollId), currentIdentity);
        pollRestEntity = RestEntityBuilder.fromPoll(poll, pollOptions);
      } catch (IllegalAccessException e) {
        LOG.warn("User {} attempt to access unauthorized poll with id {}",
                 currentIdentity.getUserId(),
                 pollId,
                 e);
      }
      activity.getLinkedProcessedEntities().put(PollUtils.POLL_ACTIVITY_TYPE, pollRestEntity);
    }
  }

}