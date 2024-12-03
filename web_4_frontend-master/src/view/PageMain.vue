<template>

  <!--график и форма-->
  <div style="float: left">

    <!--график-->
    <div class="image">
      <custom-canvas ref="canvas" @click="addDotsByGraphClick"></custom-canvas>
    </div>

    <!--форма-->
    <div class="coordinates">
      <!--координата X-->
      <div id="radioX">
        <label>Choose X Coordinate: </label><br>
        <label for="-4x"><input type="radio" value="-4" name="x-coordinate" id="-4x" @click="setX(-4)"/>-4</label>
        <label for="-3x"><input type="radio" value="-3" name="x-coordinate" id="-3x" @click="setX(-3)"/>-3</label>
        <label for="-2x"><input type="radio" value="-2" name="x-coordinate" id="-2x" @click="setX(-2)"/>-2</label>
        <label for="-1x"><input type="radio" value="-1" name="x-coordinate" id="-1x" @click="setX(-1)"/>-1</label>
        <label for="0x"><input type="radio" value="0" name="x-coordinate" id="0x" @click="setX(0)"/>0</label>
        <label for="1x"><input type="radio" value="1" name="x-coordinate" id="1x" @click="setX(1)"/>1</label>
        <label for="2x"><input type="radio" value="2" name="x-coordinate" id="2x" @click="setX(2)"/>2</label>
        <label for="3x"><input type="radio" value="3" name="x-coordinate" id="3x" @click="setX(3)"/>3</label>
        <label for="4x"><input type="radio" value="4" name="x-coordinate" id="4x" @click="setX(4)"/>4</label>
      </div>

      <!--координата Y-->
      <div id="textY">
        <label>Enter the value of the Y coordinate</label><br>
        <input
            class="input"
            v-model="inputValue"
            placeholder="in the range [-5; 3]"
            type="text"
            @input="validateInput"
            maxlength="5"
        >
        <div v-if="!isValid">Please enter a valid number in the range [-5; 3]</div>
      </div>

      <!--радиус R-->
      <div id="radioR">
        <label>Choose R Radius: </label><br>
        <label for="-4r">-<input type="radio" value="-4" name="r-coordinate" id="-4r" @click="drawing(4, points); setR(4)"/>4</label>
        <label for="-3r"><input type="radio" value="-3" name="r-coordinate" id="-3r" @click="drawing(3, points); setR(3)"/>-3</label>
        <label for="-2r"><input type="radio" value="-2" name="r-coordinate" id="-2r" @click="drawing(2, points); setR(2)"/>-2</label>
        <label for="-1r"><input type="radio" value="-1" name="r-coordinate" id="-1r" @click="drawing(1, points); setR(1)"/>-1</label>
        <label for="0r"><input type="radio" value="0" name="r-coordinate" id="0r" @click="drawing(0, points); setR(0)"/>0</label>
        <label for="1r"><input type="radio" value="1" name="r-coordinate" id="1r" @click="drawing(1, points); setR(1)"/>1</label>
        <label for="2r"><input type="radio" value="2" name="r-coordinate" id="2r" @click="drawing(2, points); setR(2)"/>2</label>
        <label for="3r"><input type="radio" value="3" name="r-coordinate" id="3r" @click="drawing(3, points); setR(3)"/>3</label>
        <label for="4r"><input type="radio" value="4" name="r-coordinate" id="4r" @click="drawing(4, points); setR(4)"/>4</label>
      </div>

    </div>

    <!--кнопка "Проверить"-->
    <form id="checkButton">
      <custom-button :disabled="!isInputValid" @click.prevent="addDotsRequest" id="checking">Confirm</custom-button>
    </form>

    <!--кнопка "Очистить"-->
    <form id="clearButton">
      <custom-button id="clearing" @click.prevent="deleteDotsRequest()">Clear</custom-button>
    </form>

  </div>

  <!--таблица-->
  <div style="float: right">
    <custom-button id="logoutButton" @click="logoutRequest">LogOut</custom-button>
    <div id="table-container">
      <table id="resultTable" class="table">
        <thead>
        <tr id="heads">
          <th>X</th>
          <th>Y</th>
          <th>R</th>
          <th>Текущее время</th>
          <th>Время работы</th>
          <th>Результат</th>
        </tr>
        </thead>
        <tr v-for="point in points" :key="point.x">
          <td>{{ point.x }}</td>
          <td>{{ point.y }}</td>
          <td>{{ point.r }}</td>
          <td>{{ point.curRequestTime }}</td>
          <td>{{ point.executionTime }}</td>
          <td>{{ point.hitType }}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
import CustomCanvas from "@/components/CustomCanvas.vue";
import CustomButton from "@/components/CustomButton.vue";
import {between, required} from "@vuelidate/validators";

export default {
  components: {CustomButton, CustomCanvas},
  data() {
    return {
      inputValue: '',
      xValue: null,
      rValue: null,
      isValid: true,
      points: []
    };
  },
  async mounted() {
    await this.getPoints();
  },
  computed: {
    isInputValid() {
      const inputValue = parseFloat(this.inputValue);
      return !isNaN(inputValue) && inputValue >= -5 && inputValue <= 3 && this.xValue != null && this.rValue != null;
    }
  },
  methods: {
    validateInput() {
      const value = parseFloat(this.inputValue);
      this.isValid = !isNaN(value) && value >= -5 && value <= 3 && this.inputValue.length <= 5;
    },
    drawing(r, points) {
      this.$refs.canvas.draw(r, points);
    },
    setX(value) {
      this.xValue = value;
    },
    setR(value) {
      this.rValue = value;
    },
    drawPointByAreaHit(r) {
      this.$refs.canvas.draw(r, this.points)
    },
    async getPoints() {
      const url = "http://localhost:4183/web_4_spring/api/dots/getDots"
      const method = 'get'
      const userResult = await this.$store.dispatch('auth/requestWithoutParams', {url, method})
      if (userResult !== null) {
        switch (userResult.status) {
          case 401:
            this.notAuthResponseHandler('Not authorized. Dots can not be loaded');
            return false;
          case 200:
            const resultArray = await userResult.json()
            this.points = resultArray.map(result => ({
              x: result.x,
              y: result.y,
              r: result.r,
              curRequestTime: result.curRequestTime,
              executionTime: result.executionTime,
              hitType: result.hitType
            }));
            this.drawPointByAreaHit(4);
            return true;
        }
      } else {
        alert('Server is down');
      }
    },
    async addDotsRequest() {
      const data = {x: this.xValue, y: parseFloat(this.inputValue), r: this.rValue}
      console.log(data);
      const url = "http://localhost:4183/web_4_spring/api/dots/add"
      const response = await this.$store.dispatch('auth/userMainPageRequest', {data, url})
      if (response === null) {
        alert('Server is down');
        return;
      }
      switch (response.status) {
        case 401:
          this.notAuthResponseHandler('Not authorized. Dots can not be loaded');
          return false;
        case 400:
          const parsedResponse = await response.json();
          alert(parsedResponse.detailMessage);
          return false;
        case 200:
          const result = await response.json();
          this.points.push({
            x: result.x,
            y: result.y,
            r: result.r,
            curRequestTime: result.curRequestTime,
            executionTime: result.executionTime,
            hitType: result.hitType });
          this.drawPointByAreaHit(this.rValue);
      }
    },
    async deleteDotsRequest() {
      const url = "http://localhost:4183/web_4_spring/api/dots/deleteDots";
      const method = 'delete'
      const response = await this.$store.dispatch('auth/requestWithoutParams', {url, method})
      if (response !== null) {
        switch (response.status) {
          case 200:
            await this.getPoints();
            break;
          case 400:
            const jsonResponse = await response.json();
            alert(jsonResponse.detailMessage);
            break;
          case 401:
            this.notAuthResponseHandler("Not authorised. Returning to login page");
            break;
        }
      } else {
        alert('Server is down')
      }
    },
    async addDotsByGraphClick(event) {
      const result = await this.$refs.canvas.handleCanvasClick(event);
      if (result !== null) {
        const { tableX, tableY } = result;
        this.xValue = tableX;
        this.inputValue = tableY;
        if (this.rValue == null) {
          alert("Choose R before clicking canvas");
        } else {
          await this.addDotsRequest();
        }
      }
    },
    notAuthResponseHandler(message) {
      alert(message);
      setTimeout(() => {
        this.$router.push("/auth");
      }, 1000)
    },
    async logoutRequest() {
      const url = "http://localhost:4183/web_4_spring/api/auth/logout"
      const data = "";
      const logoutResponse = await this.$store.dispatch('auth/userMainPageRequest', {data, url})
      if (logoutResponse !== null) {
        switch (logoutResponse.status) {
          case 200:
            const parsedLogoutResponse = await logoutResponse.json();
            this.notAuthResponseHandler(parsedLogoutResponse.message);
            localStorage.removeItem("exp_date")
            break;
        }
      }
    }
  }
}
</script>

<style scoped>
.image {
  margin-top: 20px;
  margin-left: 35px;
}

.coordinates {
  background-color: #595f72;
  margin-top: 20px;
  margin-left: 35px;
  width: 470px;
  padding: 15px;
  color: white;
  border-radius: 15px;
}

.input {
  height: 20px;
  width: 200px;
}

#checkButton {
  margin-left: 35px;
  display: inline-block; /* Это позволит форме занимать только необходимое пространство */
  margin-right: 120px; /* Расстояние между формами и кнопками */
}

#clearButton {
  display: inline-block; /* Это позволит форме занимать только необходимое пространство */
}

#checking,
#clearing,
#logoutButton {
  width: 170px;
  height: 40px;
  margin-top: 10px;
  font-size: 17px;
  border-radius: 15px;
}

.table {
  border-collapse: collapse;
  margin-top: 40px;
  margin-right: 35px;
  margin-left: 35px;
}

.table th,
.table td {
  color: black;
  border: 2px solid black;
  background-color: #39393a;
  color: white;
  padding: 8px;
  padding-left: 20px;
  padding-right: 20px;
  text-align: center;
  width: auto;
  font-weight: lighter;
}

#table-container {
  max-height: 500px; /* Устанавливаем максимальную высоту таблицы в пикселях */
  overflow-y: auto; /* Добавляем вертикальную полосу прокрутки при необходимости */
  margin-top: 20px;
}

#logoutButton {
  position: fixed; /* фиксированное положение */
  right: 30px; /* положение относительно левого края */
}

</style>