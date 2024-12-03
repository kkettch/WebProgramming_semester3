<script>
import useVuelidate from "@vuelidate/core";
import {helpers, required} from "@vuelidate/validators";
import CustomButton from "@/components/CustomButton.vue";
import CustomInput from "@/components/CustomInput.vue";

export default {
  name: "PageAuthorization",
  components: {CustomInput, CustomButton},
  data() {
    return {
      inputValueLogin: '',
      inputValuePassword: '',
      v$: useVuelidate(),
      event: {
        login: "",
        password: ""
      },
      userHasAccount: true,
    }
  },
  validations() {
    return {
      event: {
        login: {required: helpers.withMessage('Username field cannot be empty', required)},
        password: {required: helpers.withMessage('Password field cannot be empty', required)}
      }
    }
  },
  methods: {
    async userLogin() {
      this.v$.$validate();
      if (!this.v$.$error) {
        const user = {username: this.event.login, password: this.event.password}
        const url = "http://localhost:4183/web_4_spring/api/auth/login";
        const loginRequest = await this.$store.dispatch('auth/userAuthRequest', {user, url});
        if (loginRequest === null) {
          alert('Server is down, cannot login')
          return;
        }
        if (loginRequest !== undefined && loginRequest.status === 200) {
          const validResponse = await loginRequest.json()
          localStorage.setItem("exp_date", validResponse.tokenExpirationDate.toString())
          this.$router.push("/main")
        } else if (loginRequest !== undefined) {
          alert("Login or password is incorrect. Please try again")
        }
      }
    },
    async userRegister() {
      this.v$.$validate();
      if (!this.v$.$error) {
        const user = {username: this.event.login, password: this.event.password}
        const url = "http://localhost:4183/web_4_spring/api/auth/register";
        const registerRequest = await this.$store.dispatch('auth/userAuthRequest', {user, url})
        if (registerRequest !== null) {
          switch (registerRequest.status) {
            case 200:
              const validResponse = await registerRequest.json()
              localStorage.setItem("exp_date", validResponse.tokenExpirationDate.toString())
              this.$router.push("/main");
              break;
            case 400:
              const jsonResponse = await registerRequest.json();
              alert("User with this name already exist")
          }
        } else {
          console.log('Server is down, cannot register')
        }
      }
    },
    changeUserDoesntHaveAccount() {
      this.userHasAccount = false;
    },
    changeUserHasAccount() {
      this.userHasAccount = true;
    }
  },
  mounted() {
    if (localStorage.getItem("exp_date") !== null && localStorage.getItem('exp_date') < Date.now()) {
      alert('Token is expired, log in again')
    }
  }
}
</script>

<template>
  <div class="container">
    <p id="text">To start using the site, please register or log in to your account.</p>
    <h1 v-if="userHasAccount">Login</h1>
    <h1 v-else>Signup</h1>
    <form @submit.prevent>
      <div>
        <custom-input
            class="input"
            input-type="text"
            placeholder-text="Username"
            v-model:input-value.trim="event.login"
            placeholder="Username"
        />
        <p v-for="error in v$.event.login.$errors" :key="error.$uid">{{ error.$message }}</p>
      </div>
      <div>
        <custom-input
            class="input"
            input-type="password"
            placeholder-text="Password"
            v-model:input-value.trim="event.password"
            placeholder="Password"
        />
        <p v-for="error in v$.event.password.$errors" :key="error.$uid">{{ error.$message }}</p>
      </div>
      <custom-button id="login" v-if="userHasAccount" @click="userLogin">Login</custom-button>
      <custom-button id="password" v-else @click="userRegister()">Register</custom-button>
    </form>
    <div>
      <p v-if="userHasAccount">Don`t have an account? <span class="signup-link" @click="changeUserDoesntHaveAccount()">SignUp</span>
      </p>
      <p v-else>Already have an account? <span class="signup-link" @click="changeUserHasAccount()">Login</span></p>
    </div>
  </div>
</template>

<style scoped>

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100vh;
}

#login,
#password {
  width: 170px;
  height: 40px;
  margin-top: 30px;
  font-size: 17px;
  border-radius: 15px;
  align-items: center;
}

#text{
  font-size: 50px;
  font-weight: bolder;
}

</style>