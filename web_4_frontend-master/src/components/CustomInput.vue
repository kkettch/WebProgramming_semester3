<script>
export default {
  name: "custom-input",
  props: {
    label: {
      type: String,
      default: ""
    },
    inputValue: {
      type: [Number, String],
      default: ""
    },
    inputType: {
      type: String,
      default: 'text'
    },
    placeholderText: {
      type: String,
      default: ''
    }
  }
}
</script>

<template>
  <div class="input-wrap">
    <label>{{ label }}</label>
    <input
        class="input"
        :value="inputValue"
        :placeholder="placeholderText"
        :type="inputType"
        @input="$emit('update:inputValue', $event.target.value.trim())"
    >
  </div>
</template>

<style scoped>
.input {
  height: 35px;
  width: 500px; /* Задайте размер по вашему усмотрению */
  padding: 10px; /* Добавьте отступы по вашему усмотрению */
  margin-top: 5px; /* Добавьте отступы по вашему усмотрению */
  font-size:25px;
}
</style>