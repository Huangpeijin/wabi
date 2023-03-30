<template>
  <div class="contentBox"  v-if="router.currentRoute.value.path=='/'">
    <div class="imgLogo">
      <img src="../assets/logo.png" alt="mzl-ui" />
    </div>
    <h2 class="contentBox_h2">WaBi</h2>
    <h3 class="contentBox_h3">欢迎来到计算机在线学习平台</h3>
    <div class="tiBox">
<!--      <p>-->
<!--        完美支持vite+vue3版本，更小的体积，更快的响应，更强的拓展性，让我们一起向未来，拥抱这精彩的vue世界！-->
<!--      </p>-->
      <div class="buttonGroups">
        <a-button
                @click="toComponents"
                shape="round"
                size="large"
                class="btn_start"
        >
          开始学习
        </a-button>
<!--        <a-button-->
<!--                shape="round"-->
<!--                @click="goGuthub"-->
<!--                size="large"-->
<!--                class="btn_github"-->
<!--        >-->
<!--          Github-->
<!--        </a-button>-->
      </div>
      <div class="cardBox">
        <li>
          <p>面向企业就业</p>
<!--          <div>组件体积更小，灵活使用，按需使用。</div>-->
        </li>
        <li>
          <p>在线开发环境</p>
<!--          <div>配合vue3的composition API，更快的相应。</div>-->
        </li>
        <li>
          <p>参与项目开发</p>
<!--          <div>给开发者几乎所有的机会来自定义组件，满足一切项目使用场景。</div>-->
        </li>
        <li>
          <p>清晰的技术栈</p>
<!--          <div>专为vue3版本开发。</div>-->
        </li>
        <li>
          <p>友好的界面</p>
<!--          <div>极少的代码量，极简配置项。</div>-->
        </li>
      </div>
      <div class="links">
        <p class="titles">贡献人员</p>
        <li
                v-for="(item, index) in devoteList"
                :key="index"
                title="点击跳转至Ta的Github主页"
                @click="toDevoteGithub(item)"
        >
          <div class="">
            <img :src="item.img" alt="" />
            <span :style="{ color: item.name == 'Ningstyle' ? '#000' : '' }">{{
                item.name
              }}</span>
          </div>
        </li>
      </div>
    </div>
  </div>
  <RouterView></RouterView>
  <footers></footers>
</template>
<script lang="ts" setup >
  import { useRouter } from "vue-router";
  import { reactive } from "vue";
  const router = useRouter();
  const state = reactive({
    devoteList: [
      {
        name: "Huangpeijin",
        github: "https://github.com/Huangpeijin",
        img: "https://avatars.githubusercontent.com/u/116870542?v=4",
      }
    ],
  });
  const toComponents = () => {
    router.push("home");
  };
  const toDevoteGithub = (item:any) => {
    window.open(item.github);
  };
  const { devoteList } = state;
  sessionStorage.setItem("mIndex", "0-0");
  sessionStorage.setItem("tabIndex", "0");
</script>
<style lang="scss" scoped>
  .contentBox {
    width: 100%;
    height: auto;
    overflow: hidden;
    min-height: calc(100vh - 64px);
    background: rgba(9, 195, 190, 0.02);
  .contentBox_h2{
    text-align: center;
    font-family: fantasy;
    font-size: 2vw;
    color: #4a5264;
    margin: 25px 0 0 0;
  }
  .contentBox_h3{
    text-align: center;
    font-size: 2vw;
    color: #4a5264;
    margin: 25px 0 0 0;
  }
  .imgLogo {
    margin: 0 auto;
    display: block;
    width: 15vw;
    margin-top: 60px;
    position: relative;
  img {
    width: 100%;
    height: 13vw;
    display: block;
  }
  }

  h3 {
    text-align: center;
    font-size: 2vw;
    color: #4a5264;
    margin: 16px 0 0 0;
  }
  .tiBox {
    width: 70%;
    height: auto;
    overflow: hidden;
    margin: 0 auto;
    p {
      text-align: center;
      color: #636e86;
      font-size: 0.9vw;
    }
  .buttonGroups {
    width: 100%;
    text-align: center;
    height: auto;
    overflow: hidden;
    margin-top: 5vh;
    .ant-btn-round.ant-btn-lg {
      height: 60px;
      padding: 6.4px 27px;
      font-size: 18px;
      border-radius: 40px;
      /*margin-right: 90px;*/
    }
    .btn_start{
      /*margin-right: 45px;*/
    }
    .btn_github{
      margin-left: 45px;
   }
  }
  .cardBox {
    width: 100%;
    height: auto;
    overflow: hidden;
    margin-top: 5vh;
  li {
    list-style: none;
    width: 19%;
    padding: 0 5px 25px 5px;
    box-sizing: border-box;
    float: left;
    list-style: none;
    background: #fff;
    border-radius: 5px;
    box-sizing: border-box;
    transition: all 0.3s ease-in-out;
    margin-right: 1%;
    cursor: pointer;
    border-radius: 20px;
    height: 11vw;
  &:hover {
     background: rgba(9, 195, 190, 0.1);
   }
  p {
    font-size: 18px;
    color: #1f2f3d;
    font-weight: 400;
    padding: 14px 14px;

  }
  div {
    font-size: 14px;
    color: #919cac;
    padding: 0 0px;
    line-height: 20px;
    text-align: center;
  }
  }
  li:last-child {
    margin-right: 0;
  }
  }
  .links {
    width: 100%;
    height: auto;
    overflow: hidden;
    text-align: center;
    margin-bottom: 30px;
    margin-top: 1vh;
  li {
    list-style: none;
    display: inline-block;
    font-size: 0.8vw;
    font-weight: 550;
    color: #0e80eb;
    cursor: pointer;
    margin: 0 0.5vw;
  div {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 4px;
  img {
    width: 1.2vw;
    border-radius: 50%;
    margin-right: 7px;
  }
  span {
    margin: 0;
  }
  &:hover {
  span {
    text-decoration: underline;
  }
  }
  }
  }
  p {
    font-size: 20px;
    font-weight: 600;
    color: #515a6e;
    margin: 0;
    line-height: 50px;
  }
  span {
    display: inline-block;
    line-height: 30px;
    color: #0b6bc5;
    font-size: 16px;
    margin: 5px 10px 5px 10px;
    cursor: pointer;
  }
  }
  }
  }
</style>