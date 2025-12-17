


<script  setup>
import axios from "axios"
import {ref} from "vue"

const name = ref('')

 const getPua = async () =>{
try {
  const response = await axios.get("/api/customers/")
  name.value=response.data
}catch (error){
  console.log("data error")
}

}

</script>

<template>

  <div @click = 'getPua'> 寻找北京</div>
  <p>{{name}}</p>
</template>
