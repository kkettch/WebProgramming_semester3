import { createStore } from "vuex";

// модуль Vuex для управления состоянием аутентификации
const authModule = {
    namespaced: true,
    state: {
        userIsLogged: false
    },
    mutations: {
        changeUserStatus(state, userState) {
            state.userIsLogged = userState;
        }
    },
    getters: {
        getUserStatus(state) {
            return state.userIsLogged;
        }
    },
    actions: {
        async userAuthRequest({commit}, {user, url}) {
            try {
                const response = await fetch(url, {
                    credentials: 'include',
                    method: "post",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Cache': 'no-cache'
                    },
                    body: JSON.stringify(user)
                })
                commit('changeUserStatus', true)
                return response;
            } catch (ex) {
                commit('changeUserStatus', false)
                return null
            }
        },
        // Асинхронное действие для запроса на главную страницу пользователя
        async userMainPageRequest(_, { data, url }) {
            try {
                return await fetch(url, {
                    credentials: 'include',
                    method: "post",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Cache': 'no-cache'
                    },
                    body: JSON.stringify(data)
                });
            } catch (ex) {
                return null;
            }
        },
        async requestWithoutParams(_, {url, method}) {
            try {
                return await fetch(url, {
                    credentials: 'include',
                    method: method
                })
            } catch (ex) {
                return null;
            }
        },
    }
};

// Создание хранилища Vuex с включением модуля аутентификации
const store = createStore({
    modules: {
        auth: authModule
    }
});

export default store; // Экспорт созданного хранилища