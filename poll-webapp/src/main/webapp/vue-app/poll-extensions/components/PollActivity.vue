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
  <div>
    <v-card
      class="border-color border-radius my-3 pa-5"
      outlined>
      <custom-poll-activity v-bind="options" @addvote="addVote" />
    </v-card>
  </div>
</template>

<script> 
    
export default {
  data() {
    return {
      options: {
        question: 'What\'s your favourite <strong>JS</strong> framework?',
        answers: [
          { value: 1, text: 'Vue', votes: 53 ,selected: true, custom_class: 'my-3' },
          { value: 2, text: 'React', votes: 35 ,selected: false, custom_class: 'my-3' },
          { value: 3, text: 'Angular', votes: 30 ,selected: false, custom_class: 'my-3' },
          { value: 4, text: 'Other', votes: 10 ,selected: false, custom_class: 'my-3' } 
        ],
        multiple: false,
        submitButtonText: 'submit',
        showResults: false,
        finalResults: false
      }
    };
  },
  methods: {
    addVote(obj){
      console.log(obj);
    }
  }
};
</script>