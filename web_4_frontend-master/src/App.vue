<script>
export default {
  data() {
    return {
      headerProps: {
        author: "Maria Chmurova",
        group: "P3232",
        varNum: "865876"
      }
    }
  }
}


</script>

<template>
  <custom-header v-bind="headerProps"></custom-header>
  <RouterView/>
</template>

<style scoped>

</style>

<style scoped>

</style>
