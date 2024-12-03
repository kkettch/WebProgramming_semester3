<script>
export default {
  name: "custom-header",
  props: {
    author: {
      type: String,
      default: ""
    },
    group: {
      type: String,
      default: ""
    },
    varNum: {
      type: String,
      default: ""
    }
  }
}
</script>

<template>
  <section className="header">
    <div>{{ author }}</div>
    <div>{{ group }}</div>
    <div>v: {{ varNum }}</div>
  </section>
</template>

<style scoped>
.header {
  background-color: #2d3142;
  font-size: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  padding-top: 30px;
  padding-left: 60px;
  padding-right: 60px;
  border: 2px solid black;
  color: white;
}
</style>
