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
import { defineComponent } from 'vue';
import HelloWorld from '@/components/HelloWorld.vue'; // @ is an alias to /src
import axios from 'axios';
export default defineComponent({
  name: 'Home',
  setup(){
        console.log("setup");
        axios.get("http://localhost:8880/ebook/list?name=Spring").then((response)=>{
            console.log(response);
        })
    }
   });
</script>
