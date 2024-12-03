<script>
export default {
  name: "custom-button",
}
</script>

<template>
  <button class="btn">
    <slot></slot>
  </button>
</template>

<style scoped>
.btn {
  width: 38px;
  height: 32px;
  font-size: 15px;
  font-weight: bold;
  margin: 4px;
  border-radius: 10px;
}
</style>
