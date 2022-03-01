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
  <div id="custom-poll">
    <div class="vue-poll">
      <h3 class="qst" v-html="question"></h3>
      <div class="ans-cnt">
        <div
          v-for="(a,index) in calcAnswers"
          :key="index"
          :class="{ ans: true, [a.custom_class]: (a.custom_class) }">
          <template v-if="!finalResults">
            <div
              v-if="!visibleResults"
              :class="{ 'ans-no-vote noselect': true, active: a.selected }"
              @click.prevent="handleVote()">
              <span class="txt" v-html="a.text"></span>
            </div>      

            <div v-else>
              <v-progress-linear
                :value="a.percent"
                color="wight"
                height="20"
                :class="{ 'ans-voted': true, selected: a.selected }"
                rounded>
                <template>
                  <div class="flex d-flex">
                    <span
                      v-if="a.percent"
                      class="percent"
                      v-text="a.percent"></span>                  
                    <span class="txt" v-html="a.text"></span> 
                  </div>
                </template>
              </v-progress-linear>
            </div>
            <span class="bg" :style="{ width: visibleResults ? a.percent : '0%' }"></span>
          </template>

          <template v-else>
            <v-progress-linear
              :value="a.percent"
              color="wight"
              height="20"
              class="ans-voted final"
              rounded>
              <template>
                <div class="flex d-flex">
                  <span
                    v-if="a.percent"
                    class="percent"
                    v-text="a.percent"></span>
                  <span class="txt" v-html="a.text"></span> 
                </div>
              </template>
            </v-progress-linear>
            <span :class="{ bg: true, selected: mostVotes == a.votes }" :style="{ width: a.percent }"></span>
          </template>
        </div>
      </div>
      <div
        class="votes"
        v-text="reminingTime"></div>
        
      <template v-if="!finalResults && !visibleResults && multiple && totalSelections > 0">
        <a
          href="#"
          @click.prevent="handleMultiple"
          class="submit"
          v-text="submitButtonText"></a>
      </template>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    question: {
      type: String,
      required: true
    },
    answers: {
      type: Array,
      required: true
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
    multiple: {
      type: Boolean,
      default: false
    },
    submitButtonText: {
      type: String,
      default: 'Submit'
    },
    customId: {
      type: Number,
      default: 0
    }
  },
  data(){
    return {
      visibleResults: JSON.parse(this.showResults),
      d1: new Date(),
      d2: new Date('3/10/2022')
    };
  },
  computed: {
    totalVotes() {
      let totalVotes = 0;
      this.answers.filter(a=>{
        if (!isNaN(a.votes) && a.votes > 0)
        {totalVotes += parseInt(a.votes);}
      });
      return totalVotes;
    },
    reminingTime() {
      const days = this.$pollUtils.getRemainingDate.inDays(this.d1, this.d2);
      const hours = this.$pollUtils.getRemainingDate.inHours(this.d1, this.d2)-this.$pollUtils.getRemainingDate.inDays(this.d1, this.d2)*24;
      const minutes = this.$pollUtils.getRemainingDate.inMunites(this.d1, this.d2)-this.$pollUtils.getRemainingDate.inHours(this.d1, this.d2)*60;
      return this.$t('activity.poll.remaining',{0: days, 1: hours, 2: minutes});
    },
    mostVotes() {
      let max = 0;
      this.answers.filter(a=>{
        if (!isNaN(a.votes) && a.votes > 0 && a.votes >= max)
        {max = a.votes;}
      });
                
      return max;
    },
    calcAnswers() {
                               
      if (this.totalVotes === 0)
      {return this.answers.map(a=>{
        a.percent = '0%';
        return a;
      });}                    
                
      //Calculate percent
      return this.answers.filter(a=>{
        if (!isNaN(a.votes) && a.votes > 0)
        {a.percent = `${Math.round( (parseInt(a.votes)/this.totalVotes ) * 100)   }%`;}
        else
        {a.percent =  '0%';}
                                        
        return a;
      });
    },
    totalSelections() {
      return this.calcAnswers.filter(a => a.selected).length;
    }
  },
  methods: {
    handleMultiple() {
                
      const arSelected = [];
      this.calcAnswers.filter(a=>{
        if (a.selected){
          a.votes++;
          arSelected.push({ value: a.value, votes: a.votes });
        }
      });
                
      this.visibleResults = true;
                
      const obj =  { arSelected: arSelected , totalVotes: this.totalVotes };
                
      if (this.customId)
      {obj.customId = this.customId;}
                
      this.$emit('addvote', obj);
    },
    handleVote(a) {
                
      if (this.multiple){                                        
        a.selected = !a.selected;
        return;
      }
                
      a.votes++;
      a.selected = true;
      this.visibleResults = true;
                                
      const obj = { value: a.value, votes: a.votes, totalVotes: this.totalVotes };

      if (this.customId)
      {obj.customId = this.customId;}

      this.$emit('addvote', obj);
    }
  }
};
</script>