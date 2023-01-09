<template>
  <div class="home">
  <a-button type="danger">
  Danger
  </a-button>
    <img alt="Vue logo" src="../assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js + TypeScript App"/>
  </div>
</template>

<script lang="ts">

import { defineComponent, onMounted,ref} from 'vue';
import HelloWorld from '@/components/HelloWorld.vue'; // @ is an alias to /src
import axios from 'axios';

export default defineComponent({
  name: 'Home',
  setup(){
      console.log("setup");
      const ebooks = ref();
      onMounted(()=>{
        console.log("onMounted");
        axios.get("http://localhost:8880/ebook/list?name=Spring").then((response)=>{
                const  data = response.data;
                ebooks.value=data.content
                console.log(response);
        })
      })
  }
})
</script>
