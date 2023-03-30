<template>
 <headers></headers>
 <div class="chat_title">
   <h1 class="chat_title_content">The chatbot powered by OpenAI</h1>
 </div>

 <div class="chat">
  <div class="chat__window">
   <div v-for="(msg, index) in messages" :key="index" class="chat__bubble" :class="{'chat__bubble--me': msg.isMe }">
    {{ msg.text}}
   </div>
  </div>
  <form @submit.prevent="sendMessage" class="chat__input-form">
   <a-input  v-model:value="newMessage" type="text" placeholder="请输入内容..." class="chat__input-field" />
   <button type="submit" class="chat__send-button">Send</button>
  </form>
 </div>
 <footers></footers>
</template>

<script>
 import chatgptAPI from '../util/chatgpt';
 import axios from "axios";
 import {defineComponent, ref} from 'vue';
 import { createChatBotMessage } from '../util/message';
 export default defineComponent({
  setup: function () {
   const messages = ref([
    createChatBotMessage('我叫小金，是阿Kim的小助手，由OpenAI驱动的聊天机器人', false),
    createChatBotMessage('你有什么疑惑，需要我为你解决呢？', false),
   ]);
   const newMessage = ref("");
   const userMessage = ref("");
   const sendMessage = async () => {
    messages.value.push(createChatBotMessage(newMessage.value, true));
    userMessage.value = newMessage.value;
    newMessage.value = '';
    const response = await chatgptAPI.getResponse(userMessage.value);
    console.log(response);
    console.log(response.result);
    messages.value.push(createChatBotMessage(response.result, false));
   }
   return {
    newMessage,
    messages,
    sendMessage
   };
  }

 });
</script>
<style scoped>
 .chat_title{
  width: 80%;
  margin: auto;
  border-radius: 10px;
  height: 8vh;
  background: #cdc3c7;
  display: flex;
  align-items: center;
  justify-content: center;

 }
 .chat {
  display: flex;
  flex-direction: column;
  border: 1px solid;
  width: 80%;
  border-radius: 20px;
  padding: 10px;
  margin: auto;
 }
 .chat__window {
  height: calc(90vh - 200px);
  overflow-y: scroll;
  padding: 5px;
 }
 .chat_title_content{
  text-align: center;
  border-radius: 10px;
  font-style: italic;
 }
 .chat__bubble {
  padding: 10px;
  margin-bottom: 10px;
  width: fit-content;
  background-color: #c1afaf;
  border-radius: 5px;
 }
 .chat__bubble--me {
  width: fit-content;
  margin-left: auto;
  background-color: #e1d1d1;
  display: flex;
  justify-content: right;
  border-radius: 5px;
 }
 .chat__input-form {
  display: flex;
 }

 .chat__input-field {
  flex-grow: 1;
 }

 .chat__send-button {
  margin-left: 10px;
 }
</style>
