<!--
This file is part of the Meeds project (https://meeds.io/).
 
Copyright (C) 2022 Meeds Association contact@meeds.io
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <div id="poll-activity">
    <div class="poll-content">
      <h3 class="question" v-sanitized-html="poll.question"></h3>
      <div class="answer-content">
        <div
          v-for="(answer, index) in answersPercent"
          :key="index"
          :class="{ voteAnswer: true, [answer.class]: (answer.class) }">
          <template v-if="!finalResults">
            <div v-if="!visibleResults">
              <v-progress-linear
                v-if="isPollCreator"
                :value="answer.percent"
                color="wight"
                height="20"
                :class="{ 'answer-voted': true, selected: answer.voted }"
                @click.prevent="handleVote()"
                rounded>
                <template>
                  <div class="flex d-flex">
                    <span
                      v-if="answer.percent"
                      class="vote-percent"
                      v-text="answer.percent"></span>
                    <span
                      class="vote-content text-truncate"
                      :title="answer.description"
                      v-sanitized-html="answer.description"></span>
                  </div>
                </template>
              </v-progress-linear>
              <div
                :style="isPollCreator && 'margin-top: -30px'"
                :title="!isSpaceMember && $t('activity.poll.not.space.member')"
                :class="{ 'answer-cant-vote': !isSpaceMember, 'answer-no-vote no-select': true, active: answer.voted }"
                @click.prevent="handleVote(answer)">
                <span class="vote-content" v-sanitized-html="answer.description"></span>
              </div>
            </div>
            <div v-else>
              <v-progress-linear
                :value="answer.percent"
                color="wight"
                height="20"
                :class="{ 'answer-voted': true, selected: answer.voted }"
                rounded>
                <template>
                  <div class="flex d-flex">
                    <span
                      v-if="answer.percent"
                      class="vote-percent"
                      v-text="answer.percent"></span>
                    <span
                      class="vote-content text-truncate"
                      :title="answer.description"
                      v-sanitized-html="answer.description"></span>
                  </div>
                </template>
              </v-progress-linear>
            </div>
            <span class="voteBackground" :style="{ width: (!visibleResults && isPollCreator) || visibleResults ? fixWidth(answer.percent) : '0%', height: '85%', margin: '2px 2px' }"></span>
          </template>

          <template v-else>
            <v-progress-linear
              :value="answer.percent"
              color="wight"
              height="20"
              class="answer-voted final"
              rounded>
              <template>
                <div class="flex d-flex">
                  <span
                    v-if="answer.percent"
                    class="vote-percent"
                    v-text="answer.percent"></span>
                  <span
                    class="vote-content text-truncate"
                    :title="answer.description"
                    v-sanitized-html="answer.description"></span>
                </div>
              </template>
            </v-progress-linear>
            <span :class="{ voteBackground: true, selected: mostVotes === answer.votes }" :style="{ width: answer.percent }"></span>
          </template>
        </div>
      </div>
      <div
        class="total-votes"
        v-if="showTotalVotes && (visibleResults || finalResults)"
        v-text="totalVotesFormatted">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    poll: {
      type: Object,
      default: null,
    },
    showResults: {
      type: Boolean,
      default: false
    },
    showTotalVotes: {
      type: Boolean,
      default: true
    },
    finalResults: {
      type: Boolean,
      default: false
    },
    submitButtonText: {
      type: String,
      default: 'Submit'
    },
    isSpaceMember: {
      type: Boolean,
      default: false,
    },
    isPollCreator: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visibleResults: JSON.parse(this.showResults),
    };
  },
  computed: {
    answers() {
      return this.poll && this.poll.options;
    },
    totalVotes() {
      let totalVotes = 0;
      this.answers.filter(answer => {
        if (!isNaN(answer.votes) && answer.votes > 0) {
          totalVotes += parseInt(answer.votes);
        }
      });
      return totalVotes;
    },
    totalVotesFormatted(){
      return `${this.totalVotes} votes`;
    },
    mostVotes() {
      let max = 0;
      this.answers.filter(answer => {
        if (!isNaN(answer.votes) && answer.votes > 0 && answer.votes >= max) {
          max = answer.votes;
        }
      });
      return max;
    },
    answersPercent() {
      if (this.totalVotes === 0) {
        return this.answers.map(answer => {
          answer.percent = '0%';
          return answer;
        });
      }
      return this.answers.filter(answer => {
        if (!isNaN(answer.votes) && answer.votes > 0) {
          answer.percent = `${Math.round( (parseInt(answer.votes)/this.totalVotes ) * 100)}%`;
        }
        else {
          answer.percent = '0%';
        }
        return answer;
      });
    },
    isMobile() {
      return this.$vuetify.breakpoint.name === 'xs' || this.$vuetify.breakpoint.name === 'sm';
    },
  },
  methods: {
    fixWidth(percent) {
      return `${parseFloat(percent.substring(0, percent.length-1)) - (this.isMobile ? 1 : 0.5)}%`;
    },
    handleVote(answer) {
      if (this.isSpaceMember) {
        answer.votes ++;
        answer.voted = true;
        this.visibleResults = true;

        //this.$emit('submit-vote', answer.id);
      }
    }
  }
};
</script>