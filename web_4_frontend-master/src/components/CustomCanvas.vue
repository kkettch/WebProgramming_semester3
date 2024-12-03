<script>
export default {
  name: "custom-canvas",
  computed: {
    xAxis() {
      return Math.round(this.canvasPlotWidth / this.scaleX / 2) * this.scaleX
    },
    yAxis() {
      return Math.round(this.canvasPlotHeight / this.scaleY / 2) * this.scaleY
    }
  },
  data() {
    return {
      canvasPlot: null,
      ctx: null,
      defaultR: 5,
      // currentR: null,
      currentR: 5,
      canvasPlotWidth: 480,
      canvasPlotHeight: 480,
      scaleX: 40,
      scaleY: 40,
      shiftNames: 5,
      shiftAxisNames: 20
    };
  },
  mounted() {
    this.initializeCanvas();
    this.draw(4);
  },
  methods: {
    initializeCanvas() {
      this.canvasPlot = this.$refs.canvas;
      this.ctx = this.canvasPlot.getContext("2d");
    },
    draw(r, points) {
      this.ctx.clearRect(0, 0, this.canvasPlotWidth, this.canvasPlotHeight);
      this.drawGrid();
      this.drawAxes();

      this.currentR = r;
      if (this.currentR === undefined) {
        this.drawText(this.defaultR);
      } else {
        this.drawText(this.currentR);
        this.drawPolygon(this.currentR);
      }

      // Считывание данных точек из списка и рисование уже имеющихся точек на холсте!!!
      if (points !== undefined) {
        points.forEach((point) => {
          let x = point.x;
          let y = point.y;
          let color = this.validatePointForCanvas(x, y, this.currentR);
          this.drawPoint(r, x, y, color);
        });
      }
    },
    drawGrid() {
      this.ctx.beginPath();
      this.ctx.strokeStyle = "#ced0ce";

      for (let i = 0; i <= this.canvasPlotWidth; i = i + this.scaleX) {
        this.ctx.moveTo(i, 0);
        this.ctx.lineTo(i, this.canvasPlotHeight);
      }

      for (let i = 0; i <= this.canvasPlotHeight; i = i + this.scaleY) {
        this.ctx.moveTo(0, i);
        this.ctx.lineTo(this.canvasPlotWidth, i);
      }

      this.ctx.stroke();
      this.ctx.closePath();
    },
    drawAxes() {
      this.ctx.font = `${Math.round(this.scaleX / 2)}px Arial`;
      this.ctx.fillStyle = "black";
      this.ctx.beginPath();
      this.ctx.strokeStyle = "#000000";

      this.ctx.moveTo(this.xAxis, 0);
      this.ctx.lineTo(this.xAxis, this.canvasPlotHeight);
      this.ctx.fillText("y", this.xAxis - this.shiftAxisNames, this.shiftAxisNames);

      this.ctx.moveTo(0, this.yAxis);
      this.ctx.lineTo(this.canvasPlotWidth, this.yAxis);
      this.ctx.fillText("x", this.canvasPlotWidth - this.shiftAxisNames, this.yAxis - this.shiftAxisNames);

      this.ctx.stroke();
      this.ctx.closePath();
    },
    drawText(r) {
      this.ctx.fillStyle = "#4f4f4f";
      this.ctx.font = `${Math.round((r * 10) / 2)}px Arial`;

      //ось x
      this.ctx.fillText("-R", this.xAxis - this.scaleX * r + this.shiftNames, this.yAxis + this.shiftNames + this.shiftAxisNames);
      this.ctx.fillText(
          "-R/2",
          this.xAxis - (this.scaleX * r) / 2 + this.shiftNames,
          this.yAxis + this.shiftNames + this.shiftAxisNames
      );
      this.ctx.fillText(0, this.xAxis + this.shiftNames, this.yAxis + this.shiftNames + this.shiftAxisNames);
      this.ctx.fillText(
          "R/2",
          this.xAxis + (this.scaleX * r) / 2 + this.shiftNames,
          this.yAxis + this.shiftNames + this.shiftAxisNames
      );
      this.ctx.fillText("R", this.xAxis + this.scaleX * r + this.shiftNames, this.yAxis + this.shiftNames + this.shiftAxisNames);

      //ось y
      this.ctx.fillText(
          "R",
          this.xAxis + this.shiftNames,
          this.yAxis - (this.scaleY * r) + this.shiftNames  + this.shiftAxisNames
      );
      this.ctx.fillText(
          "R/2",
          this.xAxis + this.shiftNames,
          this.yAxis - (this.scaleY * r) / 2 + this.shiftNames  + this.shiftAxisNames
      );
      this.ctx.fillText(
          "-R/2",
          this.xAxis + this.shiftNames,
          this.yAxis + (this.scaleY * r) / 2 + this.shiftNames  + this.shiftAxisNames
      );
      this.ctx.fillText(
          "-R",
          this.xAxis + this.shiftNames,
          this.yAxis + (this.scaleY * r) + this.shiftNames  + this.shiftAxisNames
      );
    },
    drawPolygon(r) {
      this.drawRect(r);
      this.drawTriangle(r);
      this.drawArc(r);
    },
    drawRect(r) {
      this.ctx.beginPath();
      this.ctx.rect(this.xAxis, this.yAxis - this.scaleY * r, this.scaleX * r, this.scaleX * r);
      this.ctx.closePath();
      this.ctx.strokeStyle = "#ffba08";
      this.ctx.fillStyle = "rgba(163, 155, 168, 0.5)";
      this.ctx.fill();
      this.ctx.stroke();
    },
    drawArc(r) {
      this.ctx.beginPath();
      this.ctx.moveTo(this.xAxis, this.yAxis);
      this.ctx.arc(this.xAxis, this.yAxis, this.scaleX * r, Math.PI / 2, Math.PI, false);
      this.ctx.closePath();
      this.ctx.strokeStyle = "#ffba08";
      this.ctx.fillStyle = "rgba(163, 155, 168, 0.5)";
      this.ctx.fill();
      this.ctx.stroke();
    },
    drawTriangle(r) {
      this.ctx.beginPath();
      this.ctx.moveTo(this.xAxis, this.yAxis);
      this.ctx.lineTo(this.xAxis, this.yAxis - this.scaleX * (r / 2));
      this.ctx.lineTo(this.xAxis - this.scaleX * r, this.yAxis);
      this.ctx.closePath();
      this.ctx.strokeStyle = "#ffba08";
      this.ctx.fillStyle = "rgba(163, 155, 168, 0.5)";
      this.ctx.fill();
      this.ctx.stroke();
    },
    drawPoint(r, x, y, color) {
      console.log(x, y, r);
      this.ctx.beginPath();
      const scaledX = this.xAxis + x * this.scaleX;
      const scaledY = this.yAxis - y * this.scaleY;
      this.ctx.arc(scaledX, scaledY, 4, 0, 2 * Math.PI);
      this.ctx.fillStyle = color; // Цвет точки, например, синий
      this.ctx.fill();
      this.ctx.closePath();
    },
    handleCanvasClick(event) {
      const x = event.clientX - this.canvasPlot.getBoundingClientRect().left;
      const y = event.clientY - this.canvasPlot.getBoundingClientRect().top;

      const tableX = (x - this.xAxis) / this.scaleX;
      const tableY = (this.yAxis - y) / this.scaleY;

      if (tableX < -4 || tableX > 4) {
        alert("Значение X не соответствует указанному диапазону!");
      } else if (tableY < -5 || tableY > 3) {
        alert("Значение Y не соответствует указанному диапазону!");
      } else {
        return { tableX, tableY };
      }
      return null;
    },
    validatePointForCanvas(x, y, r) {
      {
        if ((y <= x / 2 + r / 2 && y >= 0 && x <= 0) || // Проверка попадания точки в первую область (треугольник)
            (x * x + y * y <= r * r && x <= 0 && y <= 0) || // Проверка попадания точки во вторую область (окружность)
            (x >= 0 && x <= r && y >= 0 && y <= r)) { // Проверка попадания точки в третью область (прямоугольник)
          return "green"; // Если точка попала в одну из областей, возвращаем true
        } else {
          return "red"; // Если точка не попала ни в одну из областей, возвращаем false
        }
      }
    }
  },
}

</script>

<template>
  <canvas
      id="canvas"
      :width="canvasPlotWidth"
      :height="canvasPlotHeight"
      ref="canvas"
      @click="handleCanvasClick"></canvas>
</template>

<style scoped>
#canvas {
  background-color: #e6e8e6;
  margin: auto;
}
</style>