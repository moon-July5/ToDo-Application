/*
    백엔드로 요청을 보낼 때 사용할 유틸리티 함수
*/
import { API_BASE_URL } from "../app-config";

export function call(api, method, request) {
  let options = {
    headers: new Headers({
      "Content-Type": "application/json",
    }),
    url: API_BASE_URL + api,
    method: method,
  };
  if (request) {
    // GET method
    options.body = JSON.stringify(request);
  }
  return fetch(options.url, options)
    .then((response) =>{
        if (response.status === 200){
          return response.json();
        }else{
          return Promise.reject(response);
        }
    })
    .catch((error) => {
      // 추가된 부분
      console.log(error.status);
      if (error.status === 403) {
        window.location.href = "/login"; // redirect
      }
      return Promise.reject(error);
    });
}

export function signin(userDTO){
  return call("/auth/signin", "POST", userDTO)
  .then((response) => {
    if(response.token){
      // token이 존재하는 경우 Todo 화면으로 redirect
      window.location.href = "/";
    }
  });
}