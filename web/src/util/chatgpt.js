import request from "request";

// var requestInfo={
//     method: "POST",
//     url: "https://eolink.o.apispace.com/chatgpt-turbo/create",
//     headers: {
//         "X-APISpace-Token":"fb441671b4554cf28cbe2a1e03240370",
//         "Authorization-Type":"apikey",
//         "Content-Type":"application/json"
//     },
//     body: "{\"system\":\"你是一个小助手\",\"message\":[\"user:我是孙悟空\",\"assistant:你好,悟空\",\"user:今天师傅有没有被抓走？\"],\"temperature\":\"0.9\"}"
// };

// request(requestInfo, function (error, response, body) {
//     if (error) throw new Error(error);
//     console.log(body);
// });


import axios from 'axios';
export default {
    async getResponse(message) {
        const system = "你是一个小助手";
        const temperature = 0.4
        const response = await axios({
            method: 'POST',
            url: "https://eolink.o.apispace.com/chatgpt-turbo/create",
            // data: {system,message,temperature},
            data: `{"system":"${system}","message":["user:${message}"]}`
            ,
            headers: {
                "X-APISpace-Token":"fb441671b4554cf28cbe2a1e03240370",
                "Authorization-Type":"apikey",
                "Content-Type":"application/json"
            },
            crossDomain: true
        });
        return response.data;
    },
};
// const data =ref("")
// var requestInfo = {
//     method: "POST",
//     url: "https://eolink.o.apispace.com/chatgpt-turbo/create",
//     headers: {
//         "X-APISpace-Token": "fb441671b4554cf28cbe2a1e03240370",
//         "Authorization-Type": "apikey",
//         "Content-Type": "application/json"
//     },
//     body: "{\"system\":\"你是我的一个小助手\",\"message\":[\"user:我是孙悟空\",\"assistant:你好,悟空\",\"user:今天师傅有没有被抓走？\"],\"temperature\":\"0.9\"}",
// };
// request(requestInfo, function (error, response, body) {
//     // if (error) throw new Error(error);
//     // data.value = body;
//     console.log(body);
// });
// return data.value;
